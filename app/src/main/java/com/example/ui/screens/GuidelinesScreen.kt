package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.IslamicGoldAccent
import com.example.ui.theme.IslamicGoldContainer
import com.example.ui.theme.NotoNastaliqUrduFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidelinesScreen(
    onBack: () -> Unit,
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("guidelines_screen")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isUrdu) "قواعد و ضوابط کتب خانہ" else "Library Guidelines & Rules",
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isUrdu) "واپس" else "Back",
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
            // Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreenPrimary),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = IslamicGoldAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGoldAccent)
                    ) {
                        Text(
                            text = if (isUrdu) "ام القریٰ لائبریری، ڈومن پورہ (حبہ) مئو" else "Ummul Qura Library, Domanpura (Hibba) Mau",
                            color = IslamicGoldAccent,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isUrdu) "چند ضروری ہدایات برائے قارئین و ممبران" else "Important Guidelines for Readers & Members",
                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleLarge.fontFamily,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isUrdu) "General Rules, Terms of Membership & Circulation" else "عمومی قواعد و ضوابط اور ممبرشپ کی شرائط",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Diwan Imam Shafi'i Arabic Poetry Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, IslamicGoldAccent, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "﷽",
                        fontFamily = NotoNastaliqUrduFamily,
                        fontSize = 22.sp,
                        color = IslamicGoldAccent
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Arabic Verses
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IslamicGoldContainer.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "رُفِعَتْ أَيْدٍ تَسْتَعِينُ بِالْعِلْمِ\nوَطَارَتْ أَقْدَامٌ فِي سَبِيلِهِ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreenDark,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "فَكُتُبٌ مَعَ الَّذِينَ فِي لَيْلٍ\nتُحْيِي الرُّوحَ فِي حَيَاتِهِ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreenDark,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Urdu Translation
                    Text(
                        text = "وہ ہاتھ جو علم سے مدد لیتے ہیں، بلند ہو جاتے ہیں،\nوہ قدم جو اس کے راستے پر چلتے ہیں، بلند ہو جاتے ہیں۔\nکتابیں، جو رات کی تاریکی کی طرح ہیں، روح کو زندہ کر دیتی ہیں اور زندگی میں روشنی لے آتی ہیں-",
                        fontFamily = NotoNastaliqUrduFamily,
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "— ماخوذ از دیوان امام شافعی رحمه الله",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary
                    )
                }
            }

            // Rules List
            Text(
                text = if (isUrdu) "قواعد و ضوابط کی تفصیلات" else "Library Regulations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EmeraldGreenPrimary
            )

            // Rule 1: Membership Fee
            GuidelineItemCard(
                ruleNumber = if (isUrdu) "۱" else "1",
                titlePrimary = if (isUrdu) "ماہانہ ممبرشپ فیس" else "Monthly Membership Fee",
                titleSecondary = if (isUrdu) "Monthly Membership Fee" else "ماہانہ ممبرشپ فیس",
                description = if (isUrdu) {
                    "لائبریری کے ہر ممبر کو کتاب کے اجراء کے لیے بیس روپیہ (20₹) ماہانہ دینا ہوگا، جسے ہر مہینے کے شروع میں وصول کر لیا جائے گا۔"
                } else {
                    "Each library member must pay a monthly fee of ₹20 for borrowing books, collected at the beginning of each month."
                },
                icon = Icons.Default.CurrencyRupee,
                isUrdu = isUrdu
            )

            // Rule 2: Book Issue Limit
            GuidelineItemCard(
                ruleNumber = if (isUrdu) "۲" else "2",
                titlePrimary = if (isUrdu) "بیک وقت کتاب کے اجراء کی حد" else "Issue Limit: One Book at a Time",
                titleSecondary = if (isUrdu) "Issue Limit: One Book at a Time" else "بیک وقت ایک کتاب",
                description = if (isUrdu) {
                    "بیک وقت صرف ایک ہی کتاب جاری کی جائے گی؛ ناگزیر حالات میں لائبریری کے سرپرست کی اجازت سے ایک سے زیادہ کتابیں جاری کی جا سکتی ہیں۔"
                } else {
                    "Only one book is issued at a time. In special cases, additional books may be borrowed with the permission of the library supervisor."
                },
                icon = Icons.Default.Bookmark,
                isUrdu = isUrdu
            )

            // Rule 3: Loan Period & Fine
            GuidelineItemCard(
                ruleNumber = if (isUrdu) "۳" else "3",
                titlePrimary = if (isUrdu) "مدتِ اجراء اور تاخیری جرمانہ" else "Loan Period & Overdue Fine",
                titleSecondary = if (isUrdu) "Loan Period & Overdue Fine" else "مدت اور جرمانہ",
                description = if (isUrdu) {
                    "ممبر کو ایک ہفتہ (7 دن) سے زیادہ کتاب رکھنے کا حق نہیں ہے؛ اگر زیادہ رکھنا چاہتا ہے تو ایک ہفتہ پر تجدید کرانا ضروری ہے، ورنہ یومیہ دو روپیہ (2₹) جرمانہ ادا کرنا ہوگا۔"
                } else {
                    "Borrowed books may be kept for up to 7 days. To keep a book longer, renewal is required after one week; otherwise an overdue fine of ₹2 per day applies."
                },
                icon = Icons.Default.Timer,
                highlight = true,
                isUrdu = isUrdu
            )

            // Rule 4: Requisition Process
            GuidelineItemCard(
                ruleNumber = if (isUrdu) "۴" else "4",
                titlePrimary = if (isUrdu) "کتاب طلب کرنے کا طریقہ" else "Request via Librarian Only",
                titleSecondary = if (isUrdu) "Request via Librarian Only" else "لائبریرین سے رابطہ",
                description = if (isUrdu) {
                    "مطلوبہ کتابیں فہرست دیکھ کر لائبریرین سے طلب کریں، خود الماریوں یا کتابوں کو نہ چھوئیں۔"
                } else {
                    "Please browse the catalog and request your desired books from the librarian rather than pulling books from the shelves directly."
                },
                icon = Icons.Default.PanTool,
                isUrdu = isUrdu
            )

            // Rule 5: Trust and Care of Books
            GuidelineItemCard(
                ruleNumber = if (isUrdu) "۵" else "5",
                titlePrimary = if (isUrdu) "کتابوں کی حفاظت اور قومی امانت" else "Care & Preservation of Books",
                titleSecondary = if (isUrdu) "Preservation of Books as Public Trust" else "کتابوں کی حفاظت",
                description = if (isUrdu) {
                    "کتابیں قوم کی امانت ہیں جن کی مکمل حفاظت، صفائی اور احترام آپ کی اخلاقی ذمہ داری ہے۔ کسی قسم کی تحریر، نشان یا ورق پھاڑنا سختی سے منع ہے۔"
                } else {
                    "Books are a shared community trust. Keeping them clean, intact, and free of markings or torn pages is every reader's responsibility."
                },
                icon = Icons.Default.Security,
                isUrdu = isUrdu
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun GuidelineItemCard(
    ruleNumber: String,
    titlePrimary: String,
    titleSecondary: String,
    description: String,
    icon: ImageVector,
    highlight: Boolean = false,
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) EmeraldContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (highlight) EmeraldGreenPrimary else EmeraldContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ruleNumber,
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (highlight) Color.White else EmeraldGreenDark
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = titlePrimary,
                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (highlight) EmeraldGreenPrimary else IslamicGoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = titleSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.bodyMedium.fontFamily,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
