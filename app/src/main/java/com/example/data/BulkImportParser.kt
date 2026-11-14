package com.example.data

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

enum class BulkImportErrorType {
    NONE,
    MISSING_TITLE,
    MISSING_CATALOGUE_NUMBER,
    DUPLICATE_IN_DB,
    DUPLICATE_IN_FILE
}

data class BulkImportRow(
    val tempId: String,
    val title: String,
    val author: String,
    val serialNumber: String,
    val catalogueNumber: String,
    val alphabeticalSection: String,
    val sourcePage: String,
    val category: String,
    val totalCopies: Int,
    val availableCopies: Int,
    val isValid: Boolean = false,
    val isDuplicateDb: Boolean = false,
    val isDuplicateFile: Boolean = false,
    val errorType: BulkImportErrorType = BulkImportErrorType.NONE
)

object BulkImportParser {

    val HEADER_MAP: Map<String, List<String>> = mapOf(
        "title" to listOf("title", "book title", "کتاب کا نام", "عنوان کتاب", "اسم الكتاب", "عنوان", "نام کتاب"),
        "author" to listOf("author", "book author", "مصنف", "مؤلف", "المؤلف", "الكاتب", "مصنف کا نام"),
        "serial_number" to listOf("serial number", "serial_number", "serial", "سلسلہ نمبر", "سیریل نمبر", "الرقم التسلسلي", "رقم تسلسلي", "سیریل"),
        "catalogue_number" to listOf("catalogue number", "catalogue_number", "catalogue", "فہرست نمبر", "کیٹلاگ نمبر", "رقم الفهرس", "فهرس", "کیٹلاگ"),
        "alphabetical_section" to listOf("alphabetical section", "alphabetical_section", "section", "الفبائی شعبہ", "شعبہ", "حرف", "القسم الأبجدي", "بخش"),
        "source_page" to listOf("source page", "source_page", "page", "ماخذ صفحہ", "صفحہ نمبر", "رقم الصفحة", "الصفحة", "صفحہ"),
        "category" to listOf("category", "subject", "تصنیف", "موضوع", "التصنيف", "الموضوع", "مضمون"),
        "total_copies" to listOf("total copies", "total_copies", "copies", "کل نسخے", "عدد النسخ", "نسخے"),
        "available_copies" to listOf("available copies", "available_copies", "موجود نسخے", "النسخ المتاحة")
    )

    fun parseFileBytes(
        bytes: ByteArray,
        fileName: String,
        existingCatalogueNumbers: Set<String>
    ): List<BulkImportRow> {
        val isZipXlsx = bytes.size >= 4 &&
            bytes[0] == 0x50.toByte() &&
            bytes[1] == 0x4B.toByte() &&
            bytes[2] == 0x03.toByte() &&
            bytes[3] == 0x04.toByte()

        val table: List<List<String>> = if (isZipXlsx || fileName.lowercase().endsWith(".xlsx")) {
            parseXlsxTable(ByteArrayInputStream(bytes))
        } else {
            val text = decodeTextSmart(bytes)
            parseCsvTable(text)
        }

        if (table.size < 2) return emptyList()

        val headers = table.first().map { it.trim() }
        val dataRows = table.drop(1).filter { row -> row.any { it.isNotBlank() } }

        val mapped = dataRows.mapIndexed { idx, row ->
            val rawMap = linkedMapOf<String, String>()
            headers.forEachIndexed { colIdx, headerName ->
                if (headerName.isNotBlank()) {
                    rawMap[headerName] = row.getOrNull(colIdx)?.trim().orEmpty()
                }
            }
            mapRawRowToSchema(rawMap, "row_${System.currentTimeMillis()}_$idx")
        }

        return validateRows(mapped, existingCatalogueNumbers)
    }

    fun decodeTextSmart(bytes: ByteArray): String {
        if (bytes.size >= 3 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()) {
            return String(bytes, 3, bytes.size - 3, StandardCharsets.UTF_8)
        }
        return String(bytes, StandardCharsets.UTF_8)
    }

    fun parseCsvTable(csvText: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val currentRow = mutableListOf<String>()
        val currentField = StringBuilder()
        var inQuotes = false
        var i = 0
        val clean = csvText.removePrefix("\uFEFF")

        while (i < clean.length) {
            val c = clean[i]
            when {
                inQuotes -> {
                    if (c == '"') {
                        if (i + 1 < clean.length && clean[i + 1] == '"') {
                            currentField.append('"')
                            i++
                        } else {
                            inQuotes = false
                        }
                    } else {
                        currentField.append(c)
                    }
                }
                c == '"' -> {
                    inQuotes = true
                }
                c == ',' -> {
                    currentRow.add(currentField.toString().trim())
                    currentField.setLength(0)
                }
                c == '\r' -> {
                    if (i + 1 < clean.length && clean[i + 1] == '\n') i++
                    currentRow.add(currentField.toString().trim())
                    currentField.setLength(0)
                    rows.add(currentRow.toList())
                    currentRow.clear()
                }
                c == '\n' -> {
                    currentRow.add(currentField.toString().trim())
                    currentField.setLength(0)
                    rows.add(currentRow.toList())
                    currentRow.clear()
                }
                else -> currentField.append(c)
            }
            i++
        }

        if (currentField.isNotEmpty() || currentRow.isNotEmpty()) {
            currentRow.add(currentField.toString().trim())
            rows.add(currentRow.toList())
        }

        return rows
    }

    fun parseXlsxTable(inputStream: InputStream): List<List<String>> {
        var sharedStringsXml: String? = null
        val sheetXmlCandidates = mutableMapOf<String, String>()

        ZipInputStream(inputStream).use { zis ->
            var entry: ZipEntry? = zis.nextEntry
            while (entry != null) {
                val name = entry.name.lowercase()
                if (!entry.isDirectory) {
                    val content = zis.readBytes().toString(StandardCharsets.UTF_8)
                    when {
                        name == "xl/sharedstrings.xml" -> sharedStringsXml = content
                        name.startsWith("xl/worksheets/sheet") && name.endsWith(".xml") -> {
                            sheetXmlCandidates[name] = content
                        }
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }

        val firstSheetXml = sheetXmlCandidates["xl/worksheets/sheet1.xml"]
            ?: sheetXmlCandidates.entries.sortedBy { it.key }.firstOrNull()?.value
            ?: return emptyList()

        val sharedStrings = sharedStringsXml?.let { parseSharedStrings(it) } ?: emptyList()
        return parseSheetXml(firstSheetXml, sharedStrings)
    }

    private fun parseSharedStrings(xml: String): List<String> {
        val result = mutableListOf<String>()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        var insideSi = false
        var insideT = false
        val currentText = StringBuilder()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name.lowercase()) {
                        "si" -> {
                            insideSi = true
                            currentText.setLength(0)
                        }
                        "t" -> if (insideSi) insideT = true
                    }
                }
                XmlPullParser.TEXT -> {
                    if (insideSi && insideT) {
                        currentText.append(parser.text.orEmpty())
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (parser.name.lowercase()) {
                        "t" -> insideT = false
                        "si" -> {
                            if (insideSi) {
                                result.add(currentText.toString())
                                insideSi = false
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return result
    }

    private fun parseSheetXml(xml: String, sharedStrings: List<String>): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        var currentRowMap = mutableMapOf<Int, String>()
        var maxColIndex = -1
        var currentColIndex = 0
        var currentCellType = ""
        var inCell = false
        var inValue = false
        var inInlineText = false
        val cellBuffer = StringBuilder()

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name.lowercase()) {
                        "row" -> {
                            currentRowMap = mutableMapOf()
                            maxColIndex = -1
                            currentColIndex = 0
                        }
                        "c" -> {
                            inCell = true
                            val ref = parser.getAttributeValue(null, "r")
                            currentColIndex = if (!ref.isNullOrBlank()) {
                                columnRefToIndex(ref)
                            } else {
                                currentColIndex
                            }
                            if (currentColIndex > maxColIndex) maxColIndex = currentColIndex
                            currentCellType = parser.getAttributeValue(null, "t").orEmpty()
                            cellBuffer.setLength(0)
                        }
                        "v" -> if (inCell) {
                            inValue = true
                            cellBuffer.setLength(0)
                        }
                        "t" -> if (inCell && currentCellType == "inlineStr") {
                            inInlineText = true
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    if (inValue || inInlineText) {
                        cellBuffer.append(parser.text.orEmpty())
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (parser.name.lowercase()) {
                        "v" -> inValue = false
                        "t" -> inInlineText = false
                        "c" -> {
                            if (inCell) {
                                val rawVal = cellBuffer.toString()
                                val resolved = when (currentCellType) {
                                    "s" -> {
                                        val idx = rawVal.trim().toIntOrNull()
                                        if (idx != null && idx in sharedStrings.indices) sharedStrings[idx] else rawVal
                                    }
                                    "b" -> if (rawVal.trim() == "1") "TRUE" else "FALSE"
                                    else -> rawVal
                                }
                                currentRowMap[currentColIndex] = resolved.trim()
                                currentColIndex++
                                inCell = false
                            }
                        }
                        "row" -> {
                            if (maxColIndex >= 0) {
                                val rowList = (0..maxColIndex).map { idx -> currentRowMap[idx].orEmpty() }
                                rows.add(rowList)
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return rows
    }

    private fun columnRefToIndex(cellRef: String): Int {
        var col = 0
        for (ch in cellRef) {
            if (ch.isLetter()) {
                col = col * 26 + (ch.uppercaseChar() - 'A' + 1)
            } else {
                break
            }
        }
        return (col - 1).coerceAtLeast(0)
    }

    fun mapRawRowToSchema(rawRow: Map<String, String>, tempId: String): BulkImportRow {
        fun findField(key: String): String {
            val aliases = HEADER_MAP[key].orEmpty()
            val matchedEntry = rawRow.entries.firstOrNull { (header, _) ->
                aliases.contains(header.trim().lowercase())
            }
            return matchedEntry?.value?.trim().orEmpty()
        }

        val total = findField("total_copies").toIntOrNull()?.coerceAtLeast(1) ?: 5
        val available = findField("available_copies").toIntOrNull()?.coerceIn(0, total) ?: total

        return BulkImportRow(
            tempId = tempId,
            title = findField("title"),
            author = findField("author"),
            serialNumber = findField("serial_number"),
            catalogueNumber = findField("catalogue_number"),
            alphabeticalSection = findField("alphabetical_section"),
            sourcePage = findField("source_page"),
            category = findField("category"),
            totalCopies = total,
            availableCopies = available
        )
    }

    fun validateRows(
        rows: List<BulkImportRow>,
        existingCatalogueNumbers: Set<String>
    ): List<BulkImportRow> {
        val normalizedExisting = existingCatalogueNumbers
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .toSet()

        val seenCatalogueNumbers = mutableSetOf<String>()

        return rows.map { row ->
            val title = row.title.trim()
            val catNum = row.catalogueNumber.trim()
            val catKey = catNum.lowercase()

            when {
                title.isEmpty() -> row.copy(
                    isValid = false,
                    isDuplicateDb = false,
                    isDuplicateFile = false,
                    errorType = BulkImportErrorType.MISSING_TITLE
                )
                catNum.isEmpty() -> row.copy(
                    isValid = false,
                    isDuplicateDb = false,
                    isDuplicateFile = false,
                    errorType = BulkImportErrorType.MISSING_CATALOGUE_NUMBER
                )
                normalizedExisting.contains(catKey) -> row.copy(
                    isValid = false,
                    isDuplicateDb = true,
                    isDuplicateFile = false,
                    errorType = BulkImportErrorType.DUPLICATE_IN_DB
                )
                !seenCatalogueNumbers.add(catKey) -> row.copy(
                    isValid = false,
                    isDuplicateDb = false,
                    isDuplicateFile = true,
                    errorType = BulkImportErrorType.DUPLICATE_IN_FILE
                )
                else -> row.copy(
                    isValid = true,
                    isDuplicateDb = false,
                    isDuplicateFile = false,
                    errorType = BulkImportErrorType.NONE
                )
            }
        }
    }

    fun toBookEntities(validRows: List<BulkImportRow>): List<BookEntity> {
        val timestamp = System.currentTimeMillis()
        return validRows.filter { it.isValid }.mapIndexed { idx, row ->
            val cleanTitle = row.title.trim().ifBlank { "بلا عنوان" }
            val cleanAuthor = row.author.trim().ifBlank { "نامعلوم مصنف" }
            val cleanCatNum = row.catalogueNumber.trim().ifBlank { "UQL-IMP-${1000 + idx}" }
            val cleanSerial = row.serialNumber.trim().ifBlank { "SN-IMP-${timestamp % 10000}-${idx + 1}" }
            val cleanSection = row.alphabeticalSection.trim().ifBlank {
                cleanTitle.firstOrNull()?.toString() ?: "ا"
            }
            val cleanSourcePage = row.sourcePage.trim().ifBlank { "ص 1" }
            val (categoryEng, categoryUrdu) = normalizeCategoryPair(row.category.trim())
            val total = row.totalCopies.coerceAtLeast(1)
            val avail = row.availableCopies.coerceIn(0, total)

            BookEntity(
                titleUrdu = cleanTitle,
                titleEnglish = cleanTitle,
                authorUrdu = cleanAuthor,
                authorEnglish = cleanAuthor,
                category = categoryEng,
                categoryUrdu = categoryUrdu,
                descriptionUrdu = "ام القریٰ لائبریری بلک کیٹلاگ درآمد ($cleanCatNum)۔ ماخذ صفحہ: $cleanSourcePage",
                descriptionEnglish = "Ummul Qura Library bulk catalog import ($cleanCatNum). Source page: $cleanSourcePage",
                totalCopies = total,
                availableCopies = avail,
                shelfLocation = "شعبہ $categoryUrdu - الفبائی $cleanSection",
                callNumber = cleanCatNum,
                publishedYear = "1445ھ",
                publisher = "ام القریٰ کتب خانہ، مئو",
                isAvailable = avail > 0,
                catalogueNumber = cleanCatNum,
                serialNumber = cleanSerial,
                alphabeticalSection = cleanSection,
                sourcePage = cleanSourcePage
            )
        }
    }

    fun normalizeCategoryPair(rawCategory: String): Pair<String, String> {
        if (rawCategory.isBlank()) return "General Literature" to "عمومی ادب"
        val lower = rawCategory.lowercase()
        return when {
            lower.contains("tafseer") || lower.contains("tafsir") || rawCategory.contains("تفسیر") || rawCategory.contains("تفسير") ->
                "Tafseer" to "تفسیر"
            lower.contains("hadith") || rawCategory.contains("حدیث") || rawCategory.contains("حديث") || rawCategory.contains("شمائل") ->
                "Hadith" to "حدیث"
            lower.contains("fiqh") || lower.contains("fatwa") || rawCategory.contains("فقہ") || rawCategory.contains("فقه") || rawCategory.contains("فتاوی") || rawCategory.contains("فتاوى") || rawCategory.contains("أصول") || rawCategory.contains("قواعد") ->
                "Fiqh" to "فقہ"
            lower.contains("seerat") || lower.contains("seerah") || rawCategory.contains("سیرت") || rawCategory.contains("سيرت") ->
                "Seerat" to "سیرت"
            lower.contains("history") || rawCategory.contains("تاریخ") || rawCategory.contains("تاريخ") || rawCategory.contains("تراجم") || rawCategory.contains("قصص") ->
                "History" to "تاریخ"
            lower.contains("novel") || rawCategory.contains("ناول") ->
                "Novels" to "ناول"
            lower.contains("dict") || rawCategory.contains("لغت") || rawCategory.contains("فرہنگ") ->
                "Dictionaries" to "لغت و فرہنگ"
            rawCategory.contains("عقید") || rawCategory.contains("عقيد") || rawCategory.contains("توحید") || rawCategory.contains("توحيد") || rawCategory.contains("تزکی") || rawCategory.contains("اخلاق") || rawCategory.contains("أخلاق") ->
                "General Literature" to "عمومی ادب"
            else -> rawCategory to rawCategory
        }
    }

    fun generateSampleXlsxBytes(): ByteArray {
        val headers = listOf(
            "Title",
            "Author",
            "Serial Number",
            "Catalogue Number",
            "Alphabetical Section",
            "Source Page",
            "Category",
            "Total Copies",
            "Available Copies"
        )
        val rows = listOf(
            listOf("سنن الدارمي", "الإمام عبد الله بن عبد الرحمن الدارمي", "SN-2024-051", "UQL-1051", "س", "ص ٥٠", "حدیث شریف", "5", "4"),
            listOf("مشكاة المصابيح", "الخطيب التبريزي", "SN-2024-052", "UQL-1052", "م", "ص ١٢٠", "حدیث شریف", "6", "5"),
            listOf("عقیدہ طحاویہ (اردو شرح)", "مولانا عبدالرحمن صاحب", "SN-2024-053", "UQL-1053", "ع", "ص ٨٥", "علم العقیدہ", "4", "3")
        )

        val allRows = listOf(headers) + rows
        val sheetXml = buildString {
            append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
            append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>""")
            allRows.forEachIndexed { rIdx, row ->
                val rowNum = rIdx + 1
                append("""<row r="$rowNum">""")
                row.forEachIndexed { cIdx, cellVal ->
                    val colLetter = ('A' + cIdx).toString()
                    val ref = "$colLetter$rowNum"
                    val isNumeric = rIdx > 0 && cIdx >= 7 && cellVal.toIntOrNull() != null
                    if (isNumeric) {
                        append("""<c r="$ref"><v>${cellVal}</v></c>""")
                    } else {
                        append("""<c r="$ref" t="inlineStr"><is><t>${escapeXml(cellVal)}</t></is></c>""")
                    }
                }
                append("""</row>""")
            }
            append("""</sheetData></worksheet>""")
        }

        val contentTypesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
</Types>""".trimIndent()

        val rootRelsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>""".trimIndent()

        val workbookXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="SampleBooks" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>""".trimIndent()

        val workbookRelsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
</Relationships>""".trimIndent()

        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            fun putTextEntry(path: String, content: String) {
                zos.putNextEntry(ZipEntry(path))
                zos.write(content.toByteArray(StandardCharsets.UTF_8))
                zos.closeEntry()
            }
            putTextEntry("[Content_Types].xml", contentTypesXml)
            putTextEntry("_rels/.rels", rootRelsXml)
            putTextEntry("xl/workbook.xml", workbookXml)
            putTextEntry("xl/_rels/workbook.xml.rels", workbookRelsXml)
            putTextEntry("xl/worksheets/sheet1.xml", sheetXml)
        }
        return baos.toByteArray()
    }

    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}
