package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookEntity
import com.example.ui.components.BookItemCard
import com.example.ui.components.CategoryFilterRow
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.NotoNastaliqUrduFamily

@Composable
fun CatalogScreen(
    books: List<BookEntity>,
    searchQuery: String,
    selectedCategory: String,
    selectedSection: String = "All",
    availableSections: List<String> = emptyList(),
    onSearchChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSectionSelected: (String) -> Unit = {},
    onResetFilters: () -> Unit = {
        onSearchChange("")
        onCategorySelected("All")
        onSectionSelected("All")
    },
    onBookClick: (BookEntity) -> Unit,
    onAddBookClick: () -> Unit,
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = if (isUrdu) {
                                    "کتاب، مصنف، فہرست نمبر (UQL-1001) یا سیریل نمبر تلاش کریں..."
                                } else {
                                    "Search title, author, catalogue # (UQL-1001), or serial #..."
                                },
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = EmeraldGreenPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag("catalog_search_bar"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Category filter chips
                    CategoryFilterRow(
                        selectedCategory = selectedCategory,
                        onCategorySelected = onCategorySelected,
                        isUrdu = isUrdu
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Alphabetical Section Quick Filter (All + dynamic sections from catalog)
                    val defaultSections = listOf(
                        "ا", "ب", "پ", "ت", "ج", "ح", "خ", "د", "ر", "ز",
                        "س", "ش", "ص", "ط", "ع", "ف", "ق", "ک", "ل", "م", "ن", "و", "ہ", "ی"
                    )
                    val sectionsToRender = if (availableSections.isNotEmpty()) availableSections else defaultSections

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            val isAllSelected = selectedSection == "All"
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isAllSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .testTag("section_filter_all")
                                    .clickable { onSectionSelected("All") }
                            ) {
                                Text(
                                    text = if (isUrdu) "تمام شعبے" else "All Sections",
                                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        items(sectionsToRender) { sectionChar ->
                            val isSelected = selectedSection == sectionChar
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .testTag("section_filter_$sectionChar")
                                    .clickable { onSectionSelected(sectionChar) }
                            ) {
                                Text(
                                    text = sectionChar,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Results Count & Status Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUrdu) "${books.size} کتب دستیاب ہیں" else "${books.size} Books Available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                if (selectedCategory != "All" || selectedSection != "All" || searchQuery.isNotEmpty()) {
                    Text(
                        text = if (isUrdu) "فلٹر صاف کریں" else "Reset Filters",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .testTag("reset_filters_button")
                            .clickable { onResetFilters() }
                    )
                }
            }

            // Book List
            if (books.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isUrdu) "کوئی کتاب نہیں ملی" else "No Books Found",
                            fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isUrdu) {
                                "آپ کی تلاش کے مطابق کتب دستیاب نہیں ہیں، براہ کرم دوسرا نام یا مصنف تلاش کریں۔"
                            } else {
                                "No books match your search query. Try a different title or author."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onResetFilters,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                        ) {
                            Text(if (isUrdu) "تمام کتب دکھائیں" else "Show All Books")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books, key = { it.id }) { book ->
                        BookItemCard(
                            book = book,
                            onClick = { onBookClick(book) },
                            isUrdu = isUrdu
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Add Book Floating Action Button
        FloatingActionButton(
            onClick = onAddBookClick,
            containerColor = EmeraldGreenPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_book_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = if (isUrdu) "نئی کتاب شامل کریں" else "Add Book")
        }
    }
}
