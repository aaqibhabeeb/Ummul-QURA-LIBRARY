package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookEntity
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreenDark
import com.example.ui.theme.EmeraldGreenPrimary
import com.example.ui.theme.IslamicGoldAccent
import com.example.ui.theme.NotoNastaliqUrduFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderScreen(
    book: BookEntity,
    onBack: () -> Unit,
    isUrdu: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(1) }
    val totalPages = 42
    var fontSizeSp by remember { mutableIntStateOf(18) }
    var isNightReadingMode by remember { mutableStateOf(false) }

    val readerBg = if (isNightReadingMode) Color(0xFF1A1A1A) else Color(0xFFFFFDF8) // parchment/cream
    val readerText = if (isNightReadingMode) Color(0xFFE2E2E2) else Color(0xFF1E2822)

    val pageTexts = listOf(
        // Page 1
        """
        بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ
        
        الحمد لله رب العالمين، والصلاة والسلام على أشرف الأنبیاء والمرسلين، نبينا محمد وعلى آله وصحبه أجمعين.
        
        اما بعد:
        ام القریٰ لائبریری، ڈومن پورہ {حبہ}، مئو ناتھ بھنجن کے زیر اہتمام یہ کتاب قارئین کرام کے استفادہ کے لیے پیش کی جا رہی ہے۔
        
        عنوان کتاب: ${book.titleUrdu}
        تالیف: ${book.authorUrdu}
        شعبہ: ${book.categoryUrdu} (Ummul Qura Library Collection)
        
        ${book.descriptionUrdu}
        """.trimIndent(),
        // Page 2
        """
        فصل اول: مقدمۃ الکتاب و مباحث
        
        علم دین کی طلب ہر مسلمان پر فرض ہے۔ علمائے کرام نے فرمایا ہے کہ جس طرح جسم کی بقا کے لیے غذا اور پانی ناگزیر ہے، اسی طرح روح اور ایمان کی بقا کے لیے علوم قرآنی اور سنت رسول ﷺ کا فہم لازمی ہے۔
        
        قال رسول الله ﷺ: «مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا، سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ».
        (جو شخص علم کی تلاش میں کسی راستے پر چلتا ہے، اللہ تعالیٰ اس کے لیے جنت کا راستہ آسان فرما دیتا ہے)۔
        
        اس کتاب میں ہم نے سلف صالحین کے منہج پر استدلال اور فقہی بصیرت کو عام فہم انداز میں پیش کیا ہے۔
        """.trimIndent(),
        // Page 3
        """
        فصل دوم: متن و تشریح
        
        ${book.sampleExcerptUrdu.ifBlank { "علمائے حق کا طریقہ کار ہمیشہ اعتدال اور دلائل کی روشنی میں حق بات کی تبیین رہا ہے۔\n\nاس دور میں شکوک و شبہات کے بادل چھٹ رہے ہیں اور حق واضح ہو رہا ہے۔" }}
        
        مباحث کے اہم نکات:
        1. اصل مآخذ: قرآن مجید اور سنت متواترہ
        2. فہم صحابہ و ائمہ مجتہدین
        3. عصر حاضر کے فکری و معاشرتی مسائل کا حل
        
        اور یہی ام القریٰ لائبریری کا منشور ہے کہ امت تک خالص علم و ادب پہنچایا جائے۔
        """.trimIndent()
    )

    val currentContent = pageTexts.getOrElse(currentPage - 1) {
        if (isUrdu) {
            "صفحہ نمبر $currentPage\n\nمطالعہ جاری ہے... مزید ابواب جلد دستیاب ہوں گے۔\n(ام القریٰ لائبریری ڈیجیٹل آرکائیو)"
        } else {
            "Page $currentPage\n\nReading in progress... More chapters will be available soon.\n(Ummul Qura Library Digital Archive)"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(readerBg)
            .testTag("pdf_reader_screen")
    ) {
        // Reader Top Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = if (isUrdu) book.titleUrdu else book.titleEnglish,
                        fontFamily = if (isUrdu) NotoNastaliqUrduFamily else MaterialTheme.typography.titleMedium.fontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = if (isUrdu) "صفحہ $currentPage / $totalPages" else "Page $currentPage of $totalPages",
                        style = MaterialTheme.typography.labelSmall,
                        color = IslamicGoldAccent
                    )
                }
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
            actions = {
                IconButton(onClick = { isNightReadingMode = !isNightReadingMode }) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Toggle Night/Paper Mode",
                        tint = if (isNightReadingMode) IslamicGoldAccent else Color.White
                    )
                }
                IconButton(
                    onClick = {
                        if (fontSizeSp < 26) fontSizeSp += 2
                    }
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        if (fontSizeSp > 14) fontSizeSp -= 2
                    }
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        Toast.makeText(
                            context,
                            if (isUrdu) "پی ڈی ایف کا لنک کاپی ہو گیا" else "PDF link copied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = EmeraldGreenPrimary,
                titleContentColor = Color.White
            )
        )

        // Book Content Page
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = if (isNightReadingMode) Color(0xFF333333) else IslamicGoldAccent.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = readerBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header ornament
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "﷽",
                            fontFamily = NotoNastaliqUrduFamily,
                            fontSize = 24.sp,
                            color = IslamicGoldAccent
                        )
                    }

                    Text(
                        text = currentContent,
                        fontFamily = NotoNastaliqUrduFamily,
                        fontSize = fontSizeSp.sp,
                        lineHeight = (fontSizeSp + 10).sp,
                        color = readerText,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        // Bottom Page Controls
        Surface(
            color = if (isNightReadingMode) Color(0xFF141414) else MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (currentPage > 1) currentPage-- },
                    enabled = currentPage > 1,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isUrdu) "پچھلا صفحہ" else "Previous")
                }

                Text(
                    text = if (isUrdu) "صفحہ $currentPage / $totalPages" else "Page $currentPage / $totalPages",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isNightReadingMode) Color.White else EmeraldGreenDark
                )

                Button(
                    onClick = { if (currentPage < totalPages) currentPage++ },
                    enabled = currentPage < totalPages,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isUrdu) "اگلا صفحہ" else "Next")
                }
            }
        }
    }
}
