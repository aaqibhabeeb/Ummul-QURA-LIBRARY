package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.BookEntity
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.IslamicGoldAccent
import com.example.ui.theme.NotoNastaliqUrduFamily
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBorrowed
import com.example.ui.viewmodel.AppDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopAppBar(
    titleUrdu: String = "ام القریٰ لائبریری",
    titleEnglish: String = "Ummul Qura Library",
    isRtl: Boolean,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    isAdminLoggedIn: Boolean = false,
    onAdminLogout: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isRtl) titleUrdu else titleEnglish,
                    fontFamily = if (isRtl) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isRtl) titleEnglish else titleUrdu,
                    style = MaterialTheme.typography.labelSmall,
                    color = IslamicGoldAccent,
                    letterSpacing = 1.sp
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "پیچھے جائیں / Back",
                        tint = Color.White
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_ummul_qura_logo),
                    contentDescription = "Official Ummul Qura Library Logo",
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, IslamicGoldAccent, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        },
        actions = {
            if (isAdminLoggedIn && onAdminLogout != null) {
                IconButton(
                    onClick = onAdminLogout,
                    modifier = Modifier.testTag("topbar_admin_logout_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = if (isRtl) "ایڈمن لاگ آؤٹ" else "Admin Logout",
                        tint = IslamicGoldAccent
                    )
                }
            }
            IconButton(
                onClick = onToggleLanguage,
                modifier = Modifier.testTag("toggle_language_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "اردو / English Toggle",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRtl) "English" else "اردو",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = EmeraldGreenPrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
fun LibraryBottomNavigation(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    isUrdu: Boolean = true
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_navigation_bar")
    ) {
        NavigationBarItem(
            selected = currentDestination == AppDestination.HOME,
            onClick = { onNavigate(AppDestination.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = if (isUrdu) "ہوم" else "Home") },
            label = { Text(if (isUrdu) "ہوم" else "Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreenPrimary,
                indicatorColor = EmeraldContainer
            ),
            modifier = Modifier.testTag("nav_home")
        )
        NavigationBarItem(
            selected = currentDestination == AppDestination.CATALOG,
            onClick = { onNavigate(AppDestination.CATALOG) },
            icon = { Icon(Icons.Default.AutoStories, contentDescription = if (isUrdu) "کتب خانہ" else "Catalog") },
            label = { Text(if (isUrdu) "کتب خانہ" else "Catalog", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreenPrimary,
                indicatorColor = EmeraldContainer
            ),
            modifier = Modifier.testTag("nav_catalog")
        )
        NavigationBarItem(
            selected = currentDestination == AppDestination.ISSUE_LOG,
            onClick = { onNavigate(AppDestination.ISSUE_LOG) },
            icon = { Icon(Icons.Default.Assignment, contentDescription = if (isUrdu) "اجراء رجسٹر" else "Issue Log") },
            label = { Text(if (isUrdu) "اجراء رجسٹر" else "Issue Log", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreenPrimary,
                indicatorColor = EmeraldContainer
            ),
            modifier = Modifier.testTag("nav_issue_log")
        )
        NavigationBarItem(
            selected = currentDestination == AppDestination.ADMIN_DASHBOARD ||
                    currentDestination == AppDestination.ADD_EDIT_BOOK,
            onClick = { onNavigate(AppDestination.ADMIN_DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = if (isUrdu) "ایڈمن" else "Admin") },
            label = { Text(if (isUrdu) "ایڈمن" else "Admin", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreenPrimary,
                indicatorColor = EmeraldContainer
            ),
            modifier = Modifier.testTag("nav_admin")
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    isUrdu: Boolean = true
) {
    val categories = if (isUrdu) {
        listOf(
            "All" to "تمام کتب",
            "Tafseer" to "تفسیر",
            "Hadith" to "حدیث",
            "Fiqh" to "فقہ",
            "Seerat" to "سیرت",
            "History" to "تاریخ",
            "General Literature" to "عمومی ادب",
            "Novels" to "ناول",
            "Dictionaries" to "لغت و فرہنگ"
        )
    } else {
        listOf(
            "All" to "All Books",
            "Tafseer" to "Tafseer",
            "Hadith" to "Hadith",
            "Fiqh" to "Fiqh",
            "Seerat" to "Seerat",
            "History" to "History",
            "General Literature" to "General Literature",
            "Novels" to "Novels",
            "Dictionaries" to "Dictionaries"
        )
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        categories.forEach { (catKey, catLabel) ->
            val isSelected = selectedCategory.equals(catKey, ignoreCase = true) ||
                    (selectedCategory == "All" && catKey == "All")

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(catKey) },
                label = {
                    Text(
                        text = catLabel,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldGreenPrimary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun BookItemCard(
    book: BookEntity,
    onClick: () -> Unit,
    onQuickIssue: (() -> Unit)? = null,
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("book_card_${book.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book cover preview
            BookCoverArt(
                book = book,
                isCompact = true,
                isUrdu = isUrdu,
                modifier = Modifier
                    .size(width = 72.dp, height = 100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Book Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category badge
                    Surface(
                        color = EmeraldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isUrdu) book.categoryUrdu else book.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldGreenDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Availability pill
                    Surface(
                        color = if (book.availableCopies > 0) StatusAvailable.copy(alpha = 0.12f) else StatusBorrowed.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (book.availableCopies > 0) {
                                if (isUrdu) "دستیاب (${book.availableCopies})" else "Available (${book.availableCopies})"
                            } else {
                                if (isUrdu) "تمام جاری ہیں" else "All Borrowed"
                            },
                            color = if (book.availableCopies > 0) StatusAvailable else StatusBorrowed,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isUrdu) book.titleUrdu else book.titleEnglish,
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                    fontSize = if (isUrdu) 17.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (isUrdu) book.titleEnglish else book.titleUrdu,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isUrdu) "مصنف: ${book.authorUrdu}" else "Author: ${book.authorEnglish}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldGreenPrimary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val displayCatNo = book.catalogueNumber.ifBlank { book.callNumber }
                    Text(
                        text = displayCatNo,
                        style = MaterialTheme.typography.bodySmall,
                        color = IslamicGoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (book.alphabeticalSection.isNotBlank()) {
                        Text(
                            text = if (isUrdu) "• شعبہ: ${book.alphabeticalSection}" else "• Sec: ${book.alphabeticalSection}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldGreenPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "• ${book.shelfLocation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun IssueBookDialog(
    book: BookEntity,
    onDismiss: () -> Unit,
    onConfirm: (memberName: String, memberPhone: String, memberCardNo: String, days: Int, notes: String) -> Unit,
    isUrdu: Boolean = true
) {
    var memberName by remember { mutableStateOf("") }
    var memberPhone by remember { mutableStateOf("") }
    var memberCardNo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(14) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (isUrdu) "کتاب جاری کریں" else "Issue Book",
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreenPrimary
                )
                Text(
                    text = if (isUrdu) book.titleUrdu else book.titleEnglish,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = memberName,
                    onValueChange = { memberName = it },
                    label = { Text(if (isUrdu) "قاری / ممبر کا نام *" else "Member Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("issue_member_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = memberPhone,
                    onValueChange = { memberPhone = it },
                    label = { Text(if (isUrdu) "فون نمبر *" else "Phone Number *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("issue_member_phone_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = memberCardNo,
                    onValueChange = { memberCardNo = it },
                    label = { Text(if (isUrdu) "ممبرشپ کارڈ نمبر (اختیاری)" else "Membership Card No. (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "مدت:" else "Duration:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(7, 14, 21).forEach { days ->
                            FilterChip(
                                selected = selectedDays == days,
                                onClick = { selectedDays = days },
                                label = { Text(if (isUrdu) "$days دن" else "$days Days") }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isUrdu) "نوٹس / تفصیل (اختیاری)" else "Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (memberName.isBlank() || memberPhone.isBlank()) {
                        errorText = if (isUrdu) {
                            "براہ کرم ممبر کا نام اور فون نمبر درج کریں۔"
                        } else {
                            "Please enter member name and phone number."
                        }
                    } else {
                        onConfirm(memberName.trim(), memberPhone.trim(), memberCardNo.trim(), selectedDays, notes.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                modifier = Modifier.testTag("confirm_issue_button")
            ) {
                Text(if (isUrdu) "کتاب جاری کریں" else "Confirm Issue")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isUrdu) "منسوخ کریں" else "Cancel")
            }
        }
    )
}

@Composable
fun BookCoverArt(
    book: BookEntity,
    modifier: Modifier = Modifier,
    isCompact: Boolean = true,
    isUrdu: Boolean = true
) {
    val coverColors = when (book.category.lowercase()) {
        "tafseer" -> listOf(Color(0xFF0E5338), Color(0xFF072B1C))
        "hadith" -> listOf(Color(0xFF144256), Color(0xFF0A2430))
        "fiqh" -> listOf(Color(0xFF5D1F28), Color(0xFF330E14))
        "seerat" -> listOf(Color(0xFF165B33), Color(0xFF0A331B))
        "history" -> listOf(Color(0xFF4F3822), Color(0xFF2B1D10))
        "novels" -> listOf(Color(0xFF3E2650), Color(0xFF20112B))
        "dictionaries" -> listOf(Color(0xFF1D4E4B), Color(0xFF0E2B29))
        else -> listOf(EmeraldGreenPrimary, EmeraldGreenDark)
    }

    Box(
        modifier = modifier.background(
            Brush.verticalGradient(colors = coverColors)
        )
    ) {
        if (!book.coverImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = if (isUrdu) book.titleUrdu else book.titleEnglish,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (book.coverImageRes != null) {
            AsyncImage(
                model = book.coverImageRes,
                contentDescription = if (isUrdu) book.titleUrdu else book.titleEnglish,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Subtle book spine edge on the start side
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(if (isCompact) 5.dp else 10.dp)
                    .align(Alignment.CenterStart)
                    .background(Color.Black.copy(alpha = 0.25f))
            )

            // Ornamental gold frame inside book cover
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isCompact) 5.dp else 10.dp)
                    .border(
                        width = if (isCompact) 1.dp else 1.5.dp,
                        color = IslamicGoldAccent.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(if (isCompact) 4.dp else 8.dp)
                    )
                    .padding(horizontal = if (isCompact) 5.dp else 12.dp, vertical = if (isCompact) 6.dp else 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top label: Call number or category
                    Text(
                        text = if (isUrdu) {
                            if (isCompact) book.categoryUrdu else "ام القریٰ لائبریری • ${book.categoryUrdu}"
                        } else {
                            if (isCompact) book.category else "Ummul Qura Library • ${book.category}"
                        },
                        color = IslamicGoldAccent,
                        fontSize = if (isCompact) 8.sp else 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    // Centered Book Title
                    Text(
                        text = if (isUrdu) book.titleUrdu else book.titleEnglish,
                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                        fontSize = if (isCompact) 11.sp else 17.sp,
                        lineHeight = if (isCompact) 16.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = if (isCompact) 3 else 4,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Bottom ornament / author or callNumber
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(if (isCompact) 24.dp else 56.dp)
                                .height(1.dp)
                                .background(IslamicGoldAccent.copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.height(if (isCompact) 3.dp else 6.dp))
                        Text(
                            text = if (isCompact) {
                                book.callNumber
                            } else {
                                if (isUrdu) book.authorUrdu else book.authorEnglish
                            },
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = if (isCompact) 7.sp else 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
