package com.example.ui.components

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.BookEntity
import com.example.data.BulkImportErrorType
import com.example.data.BulkImportParser
import com.example.data.BulkImportRow
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.IslamicGoldAccent
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.NotoNastaliqUrduFamily
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBorrowed

private enum class BulkFilterMode { ALL, VALID, ERRORS }

@Composable
fun BulkImportDialog(
    existingBooks: List<BookEntity>,
    onImportBooks: (List<BulkImportRow>, onComplete: (saved: Int, skipped: Int) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    isUrdu: Boolean = true
) {
    val context = LocalContext.current

    val existingCatNumbers = remember(existingBooks) {
        existingBooks.mapNotNull {
            val cat = it.catalogueNumber.ifBlank { it.callNumber }.trim().lowercase()
            cat.ifBlank { null }
        }.toSet()
    }

    var parsedRows by remember { mutableStateOf<List<BulkImportRow>>(emptyList()) }
    var selectedFileName by remember { mutableStateOf("") }
    var parseError by remember { mutableStateOf<String?>(null) }
    var filterMode by remember { mutableStateOf(BulkFilterMode.ALL) }
    var importSummary by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        parseError = null
        statusBanner = null
        importSummary = null
        try {
            val resolvedName = resolveDisplayName(context, uri) ?: "import.xlsx"
            selectedFileName = resolvedName
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes == null || bytes.isEmpty()) {
                parseError = if (isUrdu) "منتخب فائل خالی ہے۔" else "The selected file is empty."
                return@rememberLauncherForActivityResult
            }
            val parsed = BulkImportParser.parseFileBytes(bytes, resolvedName, existingCatNumbers)
            if (parsed.isEmpty()) {
                parseError = if (isUrdu) {
                    "فائل میں کتابوں کا کوئی درست ریکارڈ نہیں ملا۔ براہ کرم کالم ہیڈر چیک کریں۔"
                } else {
                    "No book rows found in the selected file. Please check column headers."
                }
            } else {
                parsedRows = parsed
            }
        } catch (e: Exception) {
            parseError = if (isUrdu) {
                "فائل پڑھنے میں خرابی پیش آئی: ${e.localizedMessage ?: "نامعلوم غلطی"}"
            } else {
                "Failed to parse file: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    val createSampleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            val bytes = BulkImportParser.generateSampleXlsxBytes()
            context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
            statusBanner = if (isUrdu) {
                "نمونہ Excel فائل کامیابی سے محفوظ ہو گئی!"
            } else {
                "Sample Excel (.xlsx) template saved successfully!"
            }
        } catch (e: Exception) {
            parseError = if (isUrdu) {
                "نمونہ فائل محفوظ کرنے میں خرابی: ${e.localizedMessage ?: ""}"
            } else {
                "Could not save sample file: ${e.localizedMessage ?: ""}"
            }
        }
    }

    val validCount = parsedRows.count { it.isValid }
    val errorCount = parsedRows.size - validCount
    val visibleRows = remember(parsedRows, filterMode) {
        when (filterMode) {
            BulkFilterMode.ALL -> parsedRows
            BulkFilterMode.VALID -> parsedRows.filter { it.isValid }
            BulkFilterMode.ERRORS -> parsedRows.filter { !it.isValid }
        }
    }

    fun updateRowAt(tempId: String, transform: (BulkImportRow) -> BulkImportRow) {
        val updated = parsedRows.map { row ->
            if (row.tempId == tempId) transform(row) else row
        }
        parsedRows = BulkImportParser.validateRows(updated, existingCatNumbers)
    }

    fun removeRow(tempId: String) {
        val remaining = parsedRows.filterNot { it.tempId == tempId }
        parsedRows = BulkImportParser.validateRows(remaining, existingCatNumbers)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("bulk_import_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldGreenDark)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableView,
                                    contentDescription = null,
                                    tint = IslamicGoldAccent
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isUrdu) "کتب کی بلک درآمد (CSV / Excel)" else "Bulk Book Import (CSV / Excel)",
                                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGoldAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isUrdu) {
                                        "CSV اور Excel (.xlsx) فائلوں سے اردو و عربی کتب شامل کریں"
                                    } else {
                                        "Import UTF-8 CSV or Excel (.xlsx) catalogues directly"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("bulk_close_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isUrdu) "بند کریں" else "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Scrollable Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // File Picker & Sample Template Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, EmeraldGreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = EmeraldGreenPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Text(
                                    text = if (isUrdu) {
                                        "تائید شدہ فارمیٹس: CSV اور Excel (.xlsx) — اردو و عربی UTF-8 محفوظ"
                                    } else {
                                        "Supported formats: CSV & Excel (.xlsx) with full Urdu/Arabic UTF-8 support"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            openDocumentLauncher.launch(
                                                arrayOf(
                                                    "text/csv",
                                                    "text/comma-separated-values",
                                                    "application/csv",
                                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                                    "application/vnd.ms-excel",
                                                    "*/*"
                                                )
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("bulk_pick_file_button")
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isUrdu) "فائل منتخب کریں" else "Select File",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            createSampleLauncher.launch("Ummul_Qura_Sample_Books.xlsx")
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("bulk_download_sample_button")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isUrdu) "نمونہ Excel" else "Sample .xlsx",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (selectedFileName.isNotBlank()) {
                                    Surface(
                                        color = EmeraldContainer,
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Description,
                                                contentDescription = null,
                                                tint = EmeraldGreenDark,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isUrdu) "منتخب فائل: $selectedFileName" else "Selected: $selectedFileName",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = EmeraldGreenDark,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!statusBanner.isNullOrBlank()) {
                        item {
                            Surface(
                                color = EmeraldContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = statusBanner!!,
                                    color = EmeraldGreenDark,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    if (!parseError.isNullOrBlank()) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = parseError!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    if (importSummary != null) {
                        val (saved, skipped) = importSummary!!
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bulk_import_summary")
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreenDark)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isUrdu) "بلک درآمد کا عمل مکمل ہو گیا!" else "Bulk Import Completed!",
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldGreenDark
                                        )
                                    }
                                    Text(
                                        text = if (isUrdu) "محفوظ کردہ کتب: $saved" else "Successfully saved: $saved",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EmeraldGreenDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (skipped > 0) {
                                        Text(
                                            text = if (isUrdu) "مسترد / مکرر صفیں: $skipped" else "Skipped / invalid rows: $skipped",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = StatusBorrowed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Filter & Save Bar
                    if (parsedRows.isNotEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = filterMode == BulkFilterMode.ALL,
                                        onClick = { filterMode = BulkFilterMode.ALL },
                                        label = { Text(if (isUrdu) "سب (${parsedRows.size})" else "All (${parsedRows.size})", fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EmeraldGreenPrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                    FilterChip(
                                        selected = filterMode == BulkFilterMode.VALID,
                                        onClick = { filterMode = BulkFilterMode.VALID },
                                        label = { Text(if (isUrdu) "درست ($validCount)" else "Ready ($validCount)", fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = StatusAvailable,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                    FilterChip(
                                        selected = filterMode == BulkFilterMode.ERRORS,
                                        onClick = { filterMode = BulkFilterMode.ERRORS },
                                        label = { Text(if (isUrdu) "اغلاط ($errorCount)" else "Errors ($errorCount)", fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.error,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (validCount == 0) {
                                            parseError = if (isUrdu) "درآمد کے لیے کوئی درست کتاب موجود نہیں ہے۔" else "No valid records to import."
                                        } else {
                                            onImportBooks(parsedRows) { saved, skipped ->
                                                importSummary = saved to skipped
                                                parsedRows = emptyList()
                                            }
                                        }
                                    },
                                    enabled = validCount > 0,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bulk_save_valid_button")
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isUrdu) {
                                            "درست کتب ڈیٹابیس میں محفوظ کریں ($validCount)"
                                        } else {
                                            "Save Valid Records to Database ($validCount)"
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        itemsIndexed(visibleRows, key = { _, item -> item.tempId }) { _, row ->
                            BulkImportPreviewRowCard(
                                row = row,
                                isUrdu = isUrdu,
                                onUpdate = { updated -> updateRowAt(row.tempId) { updated } },
                                onDelete = { removeRow(row.tempId) }
                            )
                        }
                    }
                }

                // Footer
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu) {
                                "ترمیم کر کے مکرر فہرست نمبر یا عنوان کی اصلاح کر سکتے ہیں۔"
                            } else {
                                "Edit any row inline to fix duplicate numbers or missing fields."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("bulk_close_footer_button")
                        ) {
                            Text(if (isUrdu) "بند کریں" else "Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BulkImportPreviewRowCard(
    row: BulkImportRow,
    isUrdu: Boolean,
    onUpdate: (BulkImportRow) -> Unit,
    onDelete: () -> Unit
) {
    val cardBg = when {
        row.isDuplicateDb || row.isDuplicateFile -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
        !row.isValid -> IslamicGoldContainer.copy(alpha = 0.45f)
        else -> MaterialTheme.colorScheme.surface
    }

    val statusText = when (row.errorType) {
        BulkImportErrorType.NONE -> if (isUrdu) "درست (تیار)" else "Ready"
        BulkImportErrorType.MISSING_TITLE -> if (isUrdu) "عنوان غائب ہے" else "Missing Title"
        BulkImportErrorType.MISSING_CATALOGUE_NUMBER -> if (isUrdu) "فہرست نمبر غائب ہے" else "Missing Catalogue No."
        BulkImportErrorType.DUPLICATE_IN_DB -> if (isUrdu) "ڈیٹابیس میں پہلے سے موجود" else "Duplicate in DB"
        BulkImportErrorType.DUPLICATE_IN_FILE -> if (isUrdu) "فائل میں مکرر نمبر" else "Duplicate in File"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (row.isValid) EmeraldContainer else MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (row.isValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (row.isValid) EmeraldGreenDark else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (row.isValid) EmeraldGreenDark else MaterialTheme.colorScheme.error
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = if (isUrdu) "صف حذف کریں" else "Remove row",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            OutlinedTextField(
                value = row.title,
                onValueChange = { onUpdate(row.copy(title = it)) },
                label = { Text(if (isUrdu) "عنوان کتاب *" else "Book Title *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = row.author,
                onValueChange = { onUpdate(row.copy(author = it)) },
                label = { Text(if (isUrdu) "مصنف" else "Author") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = row.catalogueNumber,
                    onValueChange = { onUpdate(row.copy(catalogueNumber = it)) },
                    label = { Text(if (isUrdu) "فہرست نمبر *" else "Catalogue No. *") },
                    isError = row.isDuplicateDb || row.isDuplicateFile || row.errorType == BulkImportErrorType.MISSING_CATALOGUE_NUMBER,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = row.serialNumber,
                    onValueChange = { onUpdate(row.copy(serialNumber = it)) },
                    label = { Text(if (isUrdu) "سیریل نمبر" else "Serial No.") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = row.alphabeticalSection,
                    onValueChange = { onUpdate(row.copy(alphabeticalSection = it)) },
                    label = { Text(if (isUrdu) "الفبائی شعبہ" else "Section") },
                    singleLine = true,
                    modifier = Modifier.weight(0.7f)
                )

                OutlinedTextField(
                    value = row.sourcePage,
                    onValueChange = { onUpdate(row.copy(sourcePage = it)) },
                    label = { Text(if (isUrdu) "ماخذ صفحہ" else "Page") },
                    singleLine = true,
                    modifier = Modifier.weight(0.8f)
                )

                OutlinedTextField(
                    value = row.category,
                    onValueChange = { onUpdate(row.copy(category = it)) },
                    label = { Text(if (isUrdu) "موضوع" else "Category") },
                    singleLine = true,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = row.totalCopies.toString(),
                    onValueChange = { v ->
                        val parsed = v.filter { it.isDigit() }.toIntOrNull()?.coerceAtLeast(1) ?: 1
                        onUpdate(row.copy(totalCopies = parsed))
                    },
                    label = { Text(if (isUrdu) "کل نسخے" else "Total Copies") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = row.availableCopies.toString(),
                    onValueChange = { v ->
                        val parsed = v.filter { it.isDigit() }.toIntOrNull()?.coerceIn(0, row.totalCopies) ?: 0
                        onUpdate(row.copy(availableCopies = parsed))
                    },
                    label = { Text(if (isUrdu) "دستیاب نسخے" else "Available Copies") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun resolveDisplayName(context: android.content.Context, uri: Uri): String? {
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0) return cursor.getString(idx)
        }
    }
    return uri.lastPathSegment
}
