package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenLight
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.IslamicGoldAccent
import com.example.ui.theme.NotoNastaliqUrduFamily

data class GalleryPhoto(
    val titleUrdu: String,
    val titleEnglish: String,
    val descriptionUrdu: String,
    val descriptionEnglish: String,
    val drawableRes: Int
)

@Composable
fun HomeScreen(
    totalBooksCount: Int,
    featuredBooks: List<BookEntity>,
    onExploreCatalog: () -> Unit,
    onBookClick: (BookEntity) -> Unit,
    onViewGuidelines: () -> Unit = {},
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    val galleryPhotos = listOf(
        GalleryPhoto(
            titleUrdu = "عمارت و سائن بورڈ",
            titleEnglish = "Library Exterior & Street Signboard",
            descriptionUrdu = "ڈومن پورہ (حبہ)، مئو ناتھ بھنجن میں واقع ام القریٰ لائبریری کا بیرونی منظر (تاسیس: 2002ء)",
            descriptionEnglish = "Exterior view of Ummul Qura Library at Domanpura (Hibba), Maunath Bhanjan (Est. 2002)",
            drawableRes = R.drawable.img_library_exterior_real
        ),
        GalleryPhoto(
            titleUrdu = "مرکزی ہال و کتب خانے",
            titleEnglish = "Main Reading Hall & Stacks",
            descriptionUrdu = "دو منزلہ وسیع کتب خانہ، بلند الماریاں اور طلبہ کے لیے پرسکون مطالعہ گاہ",
            descriptionEnglish = "Two-story reading hall with tall steel bookshelves and a quiet study environment",
            drawableRes = R.drawable.img_library_interior_hall
        ),
        GalleryPhoto(
            titleUrdu = "کتب کی قطاریں و اسٹیل ریک",
            titleEnglish = "Aisles of Steel Book Racks",
            descriptionUrdu = "ہزاروں مطبوعہ کتب کا منظم اور موضوع کے لحاظ سے ترتیب دیا گیا ذخیرہ",
            descriptionEnglish = "Well-organized aisles holding thousands of Islamic and literary books",
            drawableRes = R.drawable.img_library_shelves_aisle
        ),
        GalleryPhoto(
            titleUrdu = "شعبہ اردو ناول و ادب",
            titleEnglish = "Urdu Literature & Novels Section",
            descriptionUrdu = "ابن صفی کی عمران سیریز اور کلاسیکی اردو ناولوں کا نایاب مجموعہ",
            descriptionEnglish = "Classic Urdu novels, Ibn-e-Safi series, and literary fiction collection",
            drawableRes = R.drawable.img_library_novels_shelf
        ),
        GalleryPhoto(
            titleUrdu = "شعبہ فکریات و تراجم",
            titleEnglish = "Intellectual & Translated Works",
            descriptionUrdu = "امیر باپ غریب باپ، شاہراہ مکہ، اسلامی تاریخ اور خود آگاہی کی کتب",
            descriptionEnglish = "World history, biographies, translated classics, and self-development works",
            drawableRes = R.drawable.img_library_spines_shelf
        ),
        GalleryPhoto(
            titleUrdu = "اوپری گیلری و مطالعہ ہال",
            titleEnglish = "Mezzanine Floor & Reading Stacks",
            descriptionUrdu = "تحقیقی کام کے لیے کشادہ اوپری گیلری اور خصوصی مطالعہ کا حصہ",
            descriptionEnglish = "Spacious upper mezzanine gallery overlooking the library collection",
            drawableRes = R.drawable.img_library_mezzanine
        ),
        GalleryPhoto(
            titleUrdu = "کتب خانے کی عمودی الماریاں",
            titleEnglish = "Vertical Book Stacking",
            descriptionUrdu = "کتب کی ضخیم جلدیں اور منظم کیٹلاگ ریکارڈ",
            descriptionEnglish = "Multi-volume encyclopedias and catalogued library registers",
            drawableRes = R.drawable.img_library_book_stacks
        )
    )

    var selectedPhotoIndex by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
            ) {
                // Background real exterior image
                Image(
                    painter = painterResource(id = R.drawable.img_library_exterior_real),
                    contentDescription = "Ummul Qura Library Exterior",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Emerald Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    EmeraldGreenDark.copy(alpha = 0.65f),
                                    EmeraldGreenPrimary.copy(alpha = 0.94f)
                                )
                            )
                        )
                )

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Official Logo Emblem
                    Image(
                        painter = painterResource(id = R.drawable.img_ummul_qura_logo),
                        contentDescription = "Ummul Qura Library Logo",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, IslamicGoldAccent, CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = IslamicGoldAccent.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGoldAccent)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "فِيهَا كُتُبٌ قَيِّمَةٌ • تاسیس: 1422ھ / 2002ء",
                                color = IslamicGoldAccent,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "ام القریٰ لائبریری",
                        fontFamily = NotoNastaliqUrduFamily,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "UMMUL QURA LIBRARY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = IslamicGoldAccent,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isUrdu) "ڈومن پورہ {حبہ}، مئو ناتھ بھنجن، یو۔پی۔ انڈیا" else "Domanpura {Hibba}, Maunath Bhanjan, U.P., India",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onExploreCatalog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IslamicGoldAccent,
                            contentColor = EmeraldGreenDark
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.testTag("home_explore_catalog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "کتب خانہ دیکھیں" else "Explore Catalog",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Quick Stats Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatPill(
                    title = if (isUrdu) "کل ذخیرہ کتب" else "Collection",
                    subtitle = if (isUrdu) "$totalBooksCount کتب" else "$totalBooksCount Books",
                    icon = Icons.Default.MenuBook,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    title = if (isUrdu) "حیثیت" else "Status",
                    subtitle = if (isUrdu) "ابھی کھلا ہے" else "Open Now",
                    icon = Icons.Default.AccessTime,
                    highlight = true,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    title = if (isUrdu) "مقام" else "Location",
                    subtitle = if (isUrdu) "مئو ناتھ بھنجن" else "Maunath Bhanjan",
                    icon = Icons.Default.LocationOn,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Address & Location Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("address_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isUrdu) "پتہ و محل وقوع" else "Address & Location",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreenPrimary
                            )
                            Text(
                                text = if (isUrdu) "Domanpura {Hibba}, Maunath Bhanjan" else "ڈومن پورہ {حبہ}، مئو ناتھ بھنجن",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isUrdu) {
                            "ڈومن پورہ {حبہ}، مئو ناتھ بھنجن، ضلع مئو، اتر پردیش، انڈیا - 275101"
                        } else {
                            "Domanpura {Hibba}, Maunath Bhanjan, Dist. Mau, Uttar Pradesh, India - 275101"
                        },
                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = if (isUrdu) 17.sp else 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* Contact intent */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isUrdu) "رابطہ کریں" else "Contact", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { /* Email/Inquiry */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isUrdu) "معلومات" else "Info", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Opening Hours & Contact Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("opening_hours_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = EmeraldGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isUrdu) "لائبریری کے اوقات" else "Library Timings",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreenPrimary
                            )
                            Text(
                                text = if (isUrdu) "Opening Hours & Schedule" else "کھلنے اور بند ہونے کا وقت",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TimingRow(
                        days = if (isUrdu) "پیر تا جمعرات" else "Mon - Thu",
                        time = if (isUrdu) "صبح 8:00 تا دوپہر 1:00 | شام 3:30 تا 8:30" else "8:00 AM - 1:00 PM | 3:30 PM - 8:30 PM"
                    )
                    TimingRow(
                        days = if (isUrdu) "جمعہ" else "Friday",
                        time = if (isUrdu) "شام 3:30 تا رات 9:00 (بعد نماز جمعہ)" else "3:30 PM - 9:00 PM (After Jumu'ah)"
                    )
                    TimingRow(
                        days = if (isUrdu) "ہفتہ و اتوار" else "Sat - Sun",
                        time = if (isUrdu) "صبح 8:30 تا شام 8:30" else "8:30 AM - 8:30 PM"
                    )
                }
            }
        }

        // Introduction & Heritage
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isUrdu) "ام القریٰ لائبریری کا تعارف" else "About Ummul Qura Library",
                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isUrdu) {
                            "ام القریٰ لائبریری، ڈومن پورہ {حبہ}، مئو ناتھ بھنجن، ایک دینی، علمی اور ادبی مرکز ہے جس کا قیام اسلامی ورثے کے تحفظ اور طلبہ، علماء اور شائقین کتب کے لیے مستند کتابیں فراہم کرنے کے لیے کیا گیا ہے۔\nیہاں تفسیر قرآن، احادیث نبویہ، فقہ، سیرت طیبہ، تاریخ اور اردو ادب کا گراں قدر ذخیرہ موجود ہے۔"
                        } else {
                            "Ummul Qura Library, located at Domanpura {Hibba}, Maunath Bhanjan, is a religious, academic, and literary center dedicated to preserving Islamic heritage and providing authentic books for students, scholars, and readers.\nOur collection spans Quranic Tafseer, Hadith, Fiqh, Seerat, History, and classic Urdu Literature."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                        color = EmeraldGreenDark
                    )
                }
            }
        }

        // Library Guidelines & Circulation Rules Card (چند ضروری ہدایات)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_guidelines_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = EmeraldGreenPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isUrdu) "چند ضروری ہدایات" else "Library Guidelines",
                                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreenPrimary
                                )
                                Text(
                                    text = if (isUrdu) "Library Guidelines & Rules" else "قواعد و ضوابط",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = IslamicGoldAccent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isUrdu) "قواعد" else "Rules",
                                color = EmeraldGreenDark,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isUrdu) {
                            "• ماہانہ فیس: ہر ممبر کو کتاب کے اجراء کے لیے 20 روپے ماہانہ دینا ہوگا۔\n• کتابوں کی حد: بیک وقت صرف 1 کتاب، زیادہ سے زیادہ 1 ہفتہ کی مدت۔\n• تاخیری جرمانہ: 1 ہفتہ پر تجدید نہ کرانے پر 2 روپے یومیہ جرمانہ۔\n• کتاب طلبی: مطلوبہ کتابیں فہرست دیکھ کر لائبریرین سے طلب کریں۔\n• امانت: کتابیں قومی امانت ہیں، ان کی حفاظت ہر قاری کی ذمہ داری ہے۔"
                        } else {
                            "• Monthly Fee: ₹20 per month for borrowing books.\n• Issue Limit: 1 book at a time, maximum 7 days.\n• Overdue Fine: ₹2 per day if not renewed after 7 days.\n• Requisition: Request books via the librarian from the catalog.\n• Care: Books are a community trust—please keep them clean and safe."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onViewGuidelines,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_view_all_guidelines_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "مکمل قواعد و ضوابط پڑھیں" else "Read Full Guidelines & Rules",
                            fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.labelLarge.fontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Photo Gallery Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = EmeraldGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUrdu) "لائبریری فوٹو گیلری" else "Library Photo Gallery",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreenPrimary
                        )
                    }

                    Text(
                        text = if (isUrdu) "تصاویر" else "Photos",
                        style = MaterialTheme.typography.bodySmall,
                        color = IslamicGoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Large Preview
                val activePhoto = galleryPhotos[selectedPhotoIndex]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = activePhoto.drawableRes),
                            contentDescription = if (isUrdu) activePhoto.titleUrdu else activePhoto.titleEnglish,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.75f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isUrdu) activePhoto.titleUrdu else activePhoto.titleEnglish,
                                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isUrdu) activePhoto.descriptionUrdu else activePhoto.descriptionEnglish,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Thumbnails
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(galleryPhotos.size) { index ->
                        val photo = galleryPhotos[index]
                        val isSelected = selectedPhotoIndex == index
                        Box(
                            modifier = Modifier
                                .size(width = 85.dp, height = 65.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) EmeraldGreenPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedPhotoIndex = index }
                        ) {
                            Image(
                                painter = painterResource(id = photo.drawableRes),
                                contentDescription = if (isUrdu) photo.titleUrdu else photo.titleEnglish,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        // Featured Books Carousel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = IslamicGoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUrdu) "منتخب کتب" else "Featured Books",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreenPrimary
                        )
                    }

                    Text(
                        text = if (isUrdu) "سب دیکھیں >" else "View All >",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onExploreCatalog() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                ) {
                    items(featuredBooks.take(5)) { book ->
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .clickable { onBookClick(book) },
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column {
                                BookCoverArt(
                                    book = book,
                                    isCompact = true,
                                    isUrdu = isUrdu,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                )

                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (isUrdu) book.titleUrdu else book.titleEnglish,
                                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleSmall.fontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isUrdu) book.categoryUrdu else book.category,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EmeraldGreenPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatPill(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) EmeraldContainer else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlight) EmeraldGreenPrimary else IslamicGoldAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TimingRow(days: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = days,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = EmeraldGreenPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
