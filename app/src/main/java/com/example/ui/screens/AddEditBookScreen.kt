package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookEntity
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.IslamicGoldAccent
import com.example.ui.theme.NotoNastaliqUrduFamily

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditBookScreen(
    bookToEdit: BookEntity?,
    onSave: (
        titleUrdu: String,
        titleEnglish: String,
        authorUrdu: String,
        authorEnglish: String,
        category: String,
        categoryUrdu: String,
        descriptionUrdu: String,
        descriptionEnglish: String,
        totalCopies: Int,
        shelfLocation: String,
        callNumber: String,
        pdfUrl: String?,
        catalogueNumber: String,
        serialNumber: String,
        alphabeticalSection: String,
        sourcePage: String,
        availableCopies: Int
    ) -> Unit,
    onCancel: () -> Unit,
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isEditing = bookToEdit != null

    var titleUrdu by remember { mutableStateOf(bookToEdit?.titleUrdu ?: "") }
    var titleEnglish by remember { mutableStateOf(bookToEdit?.titleEnglish ?: "") }
    var authorUrdu by remember { mutableStateOf(bookToEdit?.authorUrdu ?: "") }
    var authorEnglish by remember { mutableStateOf(bookToEdit?.authorEnglish ?: "") }
    var category by remember { mutableStateOf(bookToEdit?.category ?: "Tafseer") }
    var categoryUrdu by remember { mutableStateOf(bookToEdit?.categoryUrdu ?: "تفسیر") }
    var descriptionUrdu by remember { mutableStateOf(bookToEdit?.descriptionUrdu ?: "") }
    var descriptionEnglish by remember { mutableStateOf(bookToEdit?.descriptionEnglish ?: "") }
    var totalCopies by remember { mutableIntStateOf(bookToEdit?.totalCopies ?: 5) }
    var availableCopies by remember { mutableIntStateOf(bookToEdit?.availableCopies ?: 5) }
    var shelfLocation by remember { mutableStateOf(bookToEdit?.shelfLocation ?: "شعبہ کتب - ریک A-01") }
    var callNumber by remember { mutableStateOf(bookToEdit?.callNumber ?: "UQ-101") }
    var catalogueNumber by remember { mutableStateOf(bookToEdit?.catalogueNumber?.ifBlank { bookToEdit.callNumber } ?: "UQL-1051") }
    var serialNumber by remember { mutableStateOf(bookToEdit?.serialNumber ?: "SN-2024-051") }
    var alphabeticalSection by remember { mutableStateOf(bookToEdit?.alphabeticalSection ?: "ا") }
    var sourcePage by remember { mutableStateOf(bookToEdit?.sourcePage ?: "ص ١") }
    var pdfUrl by remember { mutableStateOf(bookToEdit?.pdfUrl ?: "https://archive.org/details/islamic_library") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "Tafseer" to "تفسیر",
        "Hadith" to "حدیث",
        "Fiqh" to "فقہ",
        "Seerat" to "سیرت",
        "History" to "تاریخ",
        "General Literature" to "عمومی ادب",
        "Novels" to "ناول",
        "Dictionaries" to "لغت و فرہنگ"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("add_edit_book_screen")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isEditing) {
                        if (isUrdu) "کتاب کی تفصیلات میں ترمیم" else "Edit Book Details"
                    } else {
                        if (isUrdu) "نئی کتاب کا اندراج" else "Add New Book"
                    },
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isUrdu) "واپس" else "Cancel",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = EmeraldGreenPrimary,
                titleContentColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book Title Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isUrdu) "کتاب کا نام" else "Book Title",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    OutlinedTextField(
                        value = titleUrdu,
                        onValueChange = { titleUrdu = it },
                        label = { Text(if (isUrdu) "اردو عنوان کتاب *" else "Book Title (Urdu) *") },
                        leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_title_urdu"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = titleEnglish,
                        onValueChange = { titleEnglish = it },
                        label = { Text(if (isUrdu) "انگریزی عنوان (English Title)" else "Book Title (English)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_title_english"),
                        singleLine = true
                    )
                }
            }

            // Author Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isUrdu) "مصنف کی معلومات" else "Author Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    OutlinedTextField(
                        value = authorUrdu,
                        onValueChange = { authorUrdu = it },
                        label = { Text(if (isUrdu) "مصنف کا نام (اردو) *" else "Author Name (Urdu) *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_author_urdu"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = authorEnglish,
                        onValueChange = { authorEnglish = it },
                        label = { Text(if (isUrdu) "مصنف کا نام (English)" else "Author Name (English)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_author_english"),
                        singleLine = true
                    )
                }
            }

            // Category Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isUrdu) "شعبہ / زمرہ منتخب کریں *" else "Select Category *",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { (catEng, catUrd) ->
                            val isSelected = category.equals(catEng, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    category = catEng
                                    categoryUrdu = catUrd
                                },
                                label = {
                                    Text(
                                        text = if (isUrdu) catUrd else catEng,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldGreenPrimary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Placement & Copies
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isUrdu) "لائبریری میں مقام اور تعداد" else "Shelf Location & Inventory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = catalogueNumber,
                            onValueChange = { catalogueNumber = it },
                            label = { Text(if (isUrdu) "فہرست نمبر (UQL)" else "Catalogue No.") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_catalogue_number"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = serialNumber,
                            onValueChange = { serialNumber = it },
                            label = { Text(if (isUrdu) "سلسلہ / سیریل نمبر" else "Serial Number") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_serial_number"),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = alphabeticalSection,
                            onValueChange = { alphabeticalSection = it },
                            label = { Text(if (isUrdu) "الفبائی شعبہ" else "Section") },
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("input_alphabetical_section"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = sourcePage,
                            onValueChange = { sourcePage = it },
                            label = { Text(if (isUrdu) "ماخذ صفحہ" else "Source Page") },
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("input_source_page"),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = shelfLocation,
                            onValueChange = { shelfLocation = it },
                            label = { Text(if (isUrdu) "شیلف / ریک" else "Shelf / Rack") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_shelf_location"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = callNumber,
                            onValueChange = { callNumber = it },
                            label = { Text(if (isUrdu) "کال نمبر" else "Call Number") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_call_number"),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu) "کل کاپیاں:" else "Total Copies:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (totalCopies > 1) {
                                    totalCopies--
                                    if (availableCopies > totalCopies) availableCopies = totalCopies
                                }
                            }) {
                                Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "$totalCopies",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = {
                                totalCopies++
                                if (!isEditing) availableCopies = totalCopies
                            }) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu) "دستیاب کاپیاں:" else "Available Copies:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (availableCopies > 0) availableCopies-- }) {
                                Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "$availableCopies",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = { if (availableCopies < totalCopies) availableCopies++ }) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Description & Digital PDF
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isUrdu) "تعارف و ڈیجیٹل پی ڈی ایف" else "Description & PDF",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    OutlinedTextField(
                        value = descriptionUrdu,
                        onValueChange = { descriptionUrdu = it },
                        label = { Text(if (isUrdu) "کتاب کا تعارف و تفصیل (اردو)" else "Book Description (Urdu)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_description_urdu"),
                        minLines = 3,
                        maxLines = 5
                    )

                    OutlinedTextField(
                        value = descriptionEnglish,
                        onValueChange = { descriptionEnglish = it },
                        label = { Text(if (isUrdu) "انگریزی خلاصہ (English Summary)" else "Book Summary (English)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )

                    OutlinedTextField(
                        value = pdfUrl,
                        onValueChange = { pdfUrl = it },
                        label = { Text(if (isUrdu) "پی ڈی ایف فائل لنک" else "PDF File URL") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pdf_url"),
                        singleLine = true
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Save & Cancel Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (titleUrdu.isBlank()) {
                            errorMessage = if (isUrdu) {
                                "براہ کرم کتاب کا اردو عنوان درج کریں"
                            } else {
                                "Please enter the Urdu book title"
                            }
                        } else if (authorUrdu.isBlank()) {
                            errorMessage = if (isUrdu) {
                                "براہ کرم مصنف کا نام درج کریں"
                            } else {
                                "Please enter the author name"
                            }
                        } else {
                            onSave(
                                titleUrdu.trim(),
                                titleEnglish.trim().ifBlank { titleUrdu.trim() },
                                authorUrdu.trim(),
                                authorEnglish.trim().ifBlank { authorUrdu.trim() },
                                category,
                                categoryUrdu,
                                descriptionUrdu.trim().ifBlank { "کتاب برائے مطالعہ و تحقیق، ام القریٰ لائبریری" },
                                descriptionEnglish.trim().ifBlank { "Islamic literature resource available at Ummul Qura Library." },
                                totalCopies,
                                shelfLocation.trim(),
                                callNumber.trim().ifBlank { catalogueNumber.trim() },
                                pdfUrl.trim(),
                                catalogueNumber.trim().ifBlank { callNumber.trim() },
                                serialNumber.trim(),
                                alphabeticalSection.trim(),
                                sourcePage.trim(),
                                availableCopies.coerceIn(0, totalCopies)
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_book_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (isEditing) {
                            if (isUrdu) "تبدیلی محفوظ کریں" else "Save Changes"
                        } else {
                            if (isUrdu) "کتاب محفوظ کریں" else "Save Book"
                        }
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isUrdu) "منسوخ کریں" else "Cancel")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
