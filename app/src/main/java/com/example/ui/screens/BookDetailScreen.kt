package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.BookEntity
import com.example.ui.components.BookCoverArt
import com.example.ui.components.IssueBookDialog
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.IslamicGoldAccent
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.NotoNastaliqUrduFamily
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusBorrowed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookDetailScreen(
    book: BookEntity,
    onBack: () -> Unit,
    onOpenPdfReader: (BookEntity) -> Unit,
    onEditBook: (BookEntity) -> Unit,
    onDeleteBook: (BookEntity) -> Unit,
    onIssueBookConfirm: (memberName: String, memberPhone: String, memberCardNo: String, days: Int, notes: String) -> Unit,
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showIssueDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
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
                        "کیا آپ واقعی کتاب '${book.titleUrdu}' کو لائبریری سے مستقل طور پر حذف کرنا چاہتے ہیں؟"
                    } else {
                        "Are you sure you want to permanently delete '${book.titleEnglish}' from the library catalog?"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteBook(book)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_book_button")
                ) {
                    Text(if (isUrdu) "ہاں، حذف کریں" else "Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(if (isUrdu) "منسوخ کریں" else "Cancel")
                }
            }
        )
    }

    if (showIssueDialog) {
        IssueBookDialog(
            book = book,
            onDismiss = { showIssueDialog = false },
            onConfirm = { name, phone, card, days, notes ->
                showIssueDialog = false
                onIssueBookConfirm(name, phone, card, days, notes)
            },
            isUrdu = isUrdu
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("book_detail_screen")
    ) {
        // Book Cover Header with Islamic Arch Aesthetic
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(EmeraldGreenDark, EmeraldGreenPrimary)
                    )
                )
                .padding(top = 16.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                // Book Cover Frame
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(230.dp)
                        .border(2.dp, IslamicGoldAccent, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    BookCoverArt(
                        book = book,
                        isCompact = false,
                        isUrdu = isUrdu,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = if (isUrdu) book.titleUrdu else book.titleEnglish,
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.headlineSmall.fontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isUrdu) book.titleEnglish else book.titleUrdu,
                    style = MaterialTheme.typography.titleMedium,
                    color = IslamicGoldAccent,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isUrdu) "مصنف: ${book.authorUrdu}" else "Author: ${book.authorEnglish}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isUrdu) book.authorEnglish else book.authorUrdu,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        // Details Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Badges & Availability row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isUrdu) "شعبہ: ${book.categoryUrdu}" else "Category: ${book.category}",
                        color = EmeraldGreenDark,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = if (book.availableCopies > 0) StatusAvailable.copy(alpha = 0.15f) else StatusBorrowed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (book.availableCopies > 0) {
                            if (isUrdu) "دستیاب: ${book.availableCopies} / ${book.totalCopies} کاپیاں" else "Available: ${book.availableCopies} / ${book.totalCopies} Copies"
                        } else {
                            if (isUrdu) "فی الحال تمام کاپیاں جاری ہیں" else "Currently All Copies Borrowed"
                        },
                        color = if (book.availableCopies > 0) StatusAvailable else StatusBorrowed,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = IslamicGoldContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isUrdu) "مقام: ${book.shelfLocation}" else "Shelf: ${book.shelfLocation}",
                        color = Color(0xFF6B4E00),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    val catDisplay = book.catalogueNumber.ifBlank { book.callNumber }
                    Text(
                        text = if (isUrdu) "فہرست نمبر: $catDisplay" else "Catalogue No: $catDisplay",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (book.alphabeticalSection.isNotBlank()) {
                    Surface(
                        color = IslamicGoldContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isUrdu) "الفبائی شعبہ: ${book.alphabeticalSection}" else "Section: ${book.alphabeticalSection}",
                            color = Color(0xFF6B4E00),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Primary Action Buttons (PDF Read & PDF Download)
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
                        text = if (isUrdu) "مطالعہ و ڈاؤن لوڈ" else "Read & Download",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onOpenPdfReader(book) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("read_pdf_button")
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isUrdu) "آن لائن مطالعہ" else "Read PDF", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                isDownloading = true
                                Toast.makeText(
                                    context,
                                    if (isUrdu) "پی ڈی ایف '${book.titleUrdu}' ڈاؤن لوڈ ہو رہی ہے..." else "Downloading PDF '${book.titleEnglish}'...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("download_pdf_button")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isDownloading) {
                                    if (isUrdu) "ڈاؤن لوڈ ہو رہا ہے..." else "Downloading..."
                                } else {
                                    if (isUrdu) "ڈاؤن لوڈ PDF" else "Download PDF"
                                },
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Issue Book button
                    Button(
                        onClick = { showIssueDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicGoldAccent,
                            contentColor = EmeraldGreenDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("issue_book_action_button"),
                        enabled = book.availableCopies > 0
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (book.availableCopies > 0) {
                                if (isUrdu) "کتاب جاری کریں" else "Issue Book to Member"
                            } else {
                                if (isUrdu) "کتاب دستیاب نہیں" else "Out of Stock"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Description Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isUrdu) "کتاب کا تعارف" else "Book Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isUrdu) book.descriptionUrdu else book.descriptionEnglish,
                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.bodyMedium.fontFamily,
                        fontSize = if (isUrdu) 17.sp else 15.sp,
                        lineHeight = if (isUrdu) 28.sp else 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isUrdu) book.descriptionEnglish else book.descriptionUrdu,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Book Excerpt / Sample Preview Card
            if (book.sampleExcerptUrdu.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "کتاب کا اقتباس" else "Sample Excerpt",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = book.sampleExcerptUrdu,
                            fontFamily = NotoNastaliqUrduFamily,
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Publishing & Library Meta
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isUrdu) "اشاعتی معلومات" else "Publishing Info",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isUrdu) "ناشر:" else "Publisher:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(book.publisher, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isUrdu) "سال اشاعت:" else "Year:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(book.publishedYear, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }

                    if (book.serialNumber.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isUrdu) "سلسلہ نمبر:" else "Serial Number:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(book.serialNumber, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (book.sourcePage.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isUrdu) "ماخذ صفحہ:" else "Source Page:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(book.sourcePage, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isUrdu) "کال نمبر:" else "Call Number:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(book.callNumber, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isUrdu) "لائبریری:" else "Library Reference:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(if (isUrdu) "ام القریٰ لائبریری، مئو" else "Ummul Qura Library, Mau", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Admin Edit & Delete Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onEditBook(book) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("edit_book_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isUrdu) "ترمیم کریں" else "Edit")
                }

                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("delete_book_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isUrdu) "حذف کریں" else "Delete")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
