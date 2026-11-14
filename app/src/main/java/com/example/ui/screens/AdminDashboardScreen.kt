package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AdditionalCatalogData
import com.example.data.BookEntity
import com.example.data.IssueLogEntity
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.IslamicGoldAccent
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.NotoNastaliqUrduFamily
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBorrowed

@Composable
fun AdminDashboardScreen(
    totalBooksCount: Int,
    availableBooksCount: Int,
    borrowedBooksCount: Int,
    activeIssueLogs: List<IssueLogEntity>,
    books: List<BookEntity>,
    onAddBookClick: () -> Unit,
    onBulkImportClick: () -> Unit = {},
    onResetDatabaseClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onEditBookClick: (BookEntity) -> Unit,
    onDeleteBookClick: (BookEntity) -> Unit,
    onBookClick: (BookEntity) -> Unit,
    onViewIssueLogClick: () -> Unit,
    onViewCatalogClick: () -> Unit,
    onReturnBookClick: (IssueLogEntity) -> Unit,
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    var bookToDelete by remember { mutableStateOf<BookEntity?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var bookSearchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableIntStateOf(1) }
    val itemsPerPage = 10

    val totalAvailableCopiesSum = remember(books) {
        books.sumOf { it.availableCopies.coerceAtLeast(0) }
    }
    val categoriesCount = remember(books) {
        books.map { it.category.trim() }.filter { it.isNotEmpty() }.distinct().size
    }
    val sectionsCount = remember(books) {
        books.map {
            it.alphabeticalSection.ifBlank { AdditionalCatalogData.deriveSectionLetter(it.titleUrdu) }
        }.filter { it.isNotBlank() }.distinct().size
    }

    val filteredBooksList = remember(books, bookSearchQuery) {
        books.filter { book ->
            bookSearchQuery.isBlank() ||
                book.titleUrdu.contains(bookSearchQuery, ignoreCase = true) ||
                book.titleEnglish.contains(bookSearchQuery, ignoreCase = true) ||
                book.authorUrdu.contains(bookSearchQuery, ignoreCase = true) ||
                book.authorEnglish.contains(bookSearchQuery, ignoreCase = true) ||
                book.callNumber.contains(bookSearchQuery, ignoreCase = true) ||
                book.catalogueNumber.contains(bookSearchQuery, ignoreCase = true) ||
                book.serialNumber.contains(bookSearchQuery, ignoreCase = true)
        }
    }

    val totalPages = ((filteredBooksList.size + itemsPerPage - 1) / itemsPerPage).coerceAtLeast(1)
    val safePage = currentPage.coerceIn(1, totalPages)
    val paginatedBooks = remember(filteredBooksList, safePage) {
        val start = (safePage - 1) * itemsPerPage
        filteredBooksList.drop(start).take(itemsPerPage)
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    text = if (isUrdu) "ڈیٹابیس ری سیٹ کی تصدیق" else "Reset Catalogue Database",
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreenPrimary
                )
            },
            text = {
                Text(
                    text = if (isUrdu) {
                        "کیا آپ واقعی لائبریری فہرست کو اصل طے شدہ کتب پر بحال کرنا چاہتے ہیں؟"
                    } else {
                        "Are you sure you want to reset the book catalogue to the original default library books?"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog = false
                        onResetDatabaseClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                    modifier = Modifier.testTag("admin_confirm_reset_btn")
                ) {
                    Text(if (isUrdu) "ہاں، ری سیٹ کریں" else "Confirm Reset", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirmDialog = false }) {
                    Text(if (isUrdu) "منسوخ کریں" else "Cancel")
                }
            }
        )
    }

    if (bookToDelete != null) {
        val b = bookToDelete!!
        AlertDialog(
            onDismissRequest = { bookToDelete = null },
            title = {
                Text(
                    text = if (isUrdu) "کتاب حذف کرنے کی تصدیق" else "Confirm Delete Book",
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = if (isUrdu) {
                        "کیا آپ واقعی کتاب '${b.titleUrdu}' کو لائبریری سے مستقل طور پر حذف کرنا چاہتے ہیں؟"
                    } else {
                        "Are you sure you want to permanently delete '${b.titleEnglish}' from the library database?"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteBookClick(b)
                        bookToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("admin_confirm_delete_btn")
                ) {
                    Text(if (isUrdu) "ہاں، حذف کریں" else "Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { bookToDelete = null }) {
                    Text(if (isUrdu) "منسوخ کریں" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dashboard Title, Logo & Logout
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreenPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_ummul_qura_logo),
                            contentDescription = "Official Ummul Qura Library Logo",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, IslamicGoldAccent, CircleShape)
                                .background(Color.White)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isUrdu) "ایڈمن ڈیش بورڈ" else "Admin Dashboard",
                                fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isUrdu) "ام القریٰ لائبریری مینجمنٹ سسٹم" else "Ummul Qura Library Control Panel",
                                style = MaterialTheme.typography.bodySmall,
                                color = IslamicGoldAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onLogoutClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = if (isUrdu) "لاگ آؤٹ" else "Logout",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isUrdu) "لاگ آؤٹ" else "Logout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4 Key Metrics Grid (Total Books, Available Copies, Categories, Alphabetical Sections)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isUrdu) "لائبریری کا جائزہ" else "Library Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreenPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        count = totalBooksCount.toString(),
                        primaryLabel = if (isUrdu) "کل کتب فہرست" else "Total Catalogued",
                        secondaryLabel = if (isUrdu) "دستیاب ٹائٹلز: $availableBooksCount" else "Available Titles: $availableBooksCount",
                        icon = Icons.Default.LibraryBooks,
                        containerColor = EmeraldContainer,
                        contentColor = EmeraldGreenDark,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_total_books")
                    )

                    MetricCard(
                        count = totalAvailableCopiesSum.toString(),
                        primaryLabel = if (isUrdu) "دستیاب نسخے" else "Available Copies",
                        secondaryLabel = if (isUrdu) "جاری شدہ کتب: $borrowedBooksCount" else "Borrowed: $borrowedBooksCount",
                        icon = Icons.Default.MenuBook,
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = StatusAvailable,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_available_copies")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        count = categoriesCount.toString(),
                        primaryLabel = if (isUrdu) "موضوعات / شعبے" else "Subject Categories",
                        secondaryLabel = if (isUrdu) "کل موضوعات" else "Categories",
                        icon = Icons.Default.Category,
                        containerColor = Color(0xFFE3F2FD),
                        contentColor = Color(0xFF1565C0),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_categories_count")
                    )

                    MetricCard(
                        count = sectionsCount.toString(),
                        primaryLabel = if (isUrdu) "الفبائی شعبے" else "Alpha Sections",
                        secondaryLabel = if (isUrdu) "حروف تہجی" else "Sections",
                        icon = Icons.Default.SortByAlpha,
                        containerColor = IslamicGoldContainer,
                        contentColor = Color(0xFF6B4E00),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_sections_count")
                    )
                }
            }
        }

        // Quick Management Actions (Bulk Import, Add Book, Issue Log, Reset DB)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isUrdu) "فوری اقدامات" else "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    // Prominent Bulk CSV/Excel Import button
                    Button(
                        onClick = onBulkImportClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_bulk_import_button")
                    ) {
                        Icon(
                            Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = IslamicGoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "کتب کی بلک درآمد (CSV / Excel Import)" else "Bulk CSV / Excel Import",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onAddBookClick,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_add_book_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUrdu) "نئی کتاب" else "Add Book", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onViewIssueLogClick,
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGoldAccent, contentColor = EmeraldGreenDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_view_logs_button")
                        ) {
                            Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isUrdu) "اجراء ($borrowedBooksCount)" else "Issue Log ($borrowedBooksCount)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { showResetConfirmDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("admin_reset_db_button")
                        ) {
                            Icon(
                                Icons.Default.RestartAlt,
                                contentDescription = if (isUrdu) "ڈیٹابیس ری سیٹ" else "Reset Database",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Borrowers & Circulation Snapshot
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUrdu) "جاری شدہ کتب کی فہرست (${activeIssueLogs.size})" else "Currently Borrowed (${activeIssueLogs.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreenPrimary
                )

                Text(
                    text = if (isUrdu) "مکمل رجسٹر دیکھیں >" else "View Full Log >",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldGreenPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onViewIssueLogClick() }
                )
            }
        }

        if (activeIssueLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isUrdu) {
                                "فی الحال کوئی کتاب جاری نہیں ہے، تمام کتب شیلف پر دستیاب ہیں۔"
                            } else {
                                "No books are currently borrowed. All books are available on shelves."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(activeIssueLogs.take(3), key = { it.id }) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isUrdu) log.bookTitleUrdu else log.bookTitle,
                                fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isUrdu) {
                                    "ممبر: ${log.memberName} (${log.memberPhone})"
                                } else {
                                    "Member: ${log.memberName} (${log.memberPhone})"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = EmeraldGreenPrimary
                            )
                            Text(
                                text = if (isUrdu) {
                                    "تاریخِ اجراء: ${log.issueDate} | واپسی: ${log.dueDate}"
                                } else {
                                    "Issued: ${log.issueDate} | Due: ${log.dueDate}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = { onReturnBookClick(log) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("dashboard_return_btn_${log.id}")
                        ) {
                            Text(if (isUrdu) "واپسی درج کریں" else "Mark Returned", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section: Book Management (Search, Pagination, Edit & Delete)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "کتب کا انتظام (${filteredBooksList.size})" else "Book Management (${filteredBooksList.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    Button(
                        onClick = onAddBookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isUrdu) "نئی کتاب" else "Add Book", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bookSearchQuery,
                    onValueChange = {
                        bookSearchQuery = it
                        currentPage = 1
                    },
                    placeholder = {
                        Text(
                            text = if (isUrdu) {
                                "نام، مصنف، فہرست نمبر یا سیریل نمبر تلاش کریں..."
                            } else {
                                "Search by title, author, catalogue #, or serial #..."
                            },
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldGreenPrimary) },
                    trailingIcon = {
                        if (bookSearchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                bookSearchQuery = ""
                                currentPage = 1
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_book_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Paginated books list
        items(paginatedBooks, key = { "admin_book_${it.id}" }) { book ->
            val displayCatNo = book.catalogueNumber.ifBlank { book.callNumber }
            val displaySection = book.alphabeticalSection.ifBlank {
                AdditionalCatalogData.deriveSectionLetter(book.titleUrdu)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBookClick(book) }
                    .testTag("admin_book_row_${book.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = EmeraldContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = displayCatNo,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldGreenDark,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = IslamicGoldContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isUrdu) "شعبہ: $displaySection" else "Sec: $displaySection",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF6B4E00),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isUrdu) book.categoryUrdu else book.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isUrdu) {
                                "دستیاب: ${book.availableCopies} / ${book.totalCopies}"
                            } else {
                                "Avail: ${book.availableCopies} / ${book.totalCopies}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (book.availableCopies > 0) StatusAvailable else StatusBorrowed,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isUrdu) book.titleUrdu else book.titleEnglish,
                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (isUrdu) {
                            "مصنف: ${book.authorUrdu} • ${book.serialNumber.ifBlank { book.callNumber }} • ${book.sourcePage}"
                        } else {
                            "Author: ${book.authorEnglish} • ${book.serialNumber.ifBlank { book.callNumber }} • ${book.sourcePage}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onEditBookClick(book) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_edit_btn_${book.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUrdu) "ترمیم کریں" else "Edit", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = { bookToDelete = book },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_delete_btn_${book.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isUrdu) "حذف کریں" else "Delete", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Pagination Controls
        if (totalPages > 1) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (safePage > 1) currentPage = safePage - 1 },
                        enabled = safePage > 1,
                        modifier = Modifier.testTag("admin_prev_page_btn")
                    ) {
                        Text(if (isUrdu) "پچھلا صفحہ" else "Previous", fontSize = 12.sp)
                    }

                    Text(
                        text = if (isUrdu) "صفحہ $safePage از $totalPages" else "Page $safePage of $totalPages",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    OutlinedButton(
                        onClick = { if (safePage < totalPages) currentPage = safePage + 1 },
                        enabled = safePage < totalPages,
                        modifier = Modifier.testTag("admin_next_page_btn")
                    ) {
                        Text(if (isUrdu) "اگلا صفحہ" else "Next", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun MetricCard(
    count: String,
    primaryLabel: String,
    secondaryLabel: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = primaryLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = secondaryLabel,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
