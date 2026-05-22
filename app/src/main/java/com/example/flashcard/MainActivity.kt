package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.theme.FlashcardTheme
import com.example.flashcard.ui.theme.IndigoPrimary
import com.example.flashcard.ui.theme.VioletSecondary
import com.example.flashcard.ui.theme.IndigoContainer
import com.example.flashcard.ui.theme.EmeraldGreen
import com.example.flashcard.ui.theme.RoseRed
import com.example.flashcard.ui.theme.AmberYellow

data class FlashcardWord(
    val word: String,
    val meaning: String,
    val example: String = "",
    val isMastered: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0
)

enum class Screen {
    HOME,
    ADD_WORD,
    WORD_LIST,
    REVIEW,
    STATS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardTheme {
                FlashcardApp()
            }
        }
    }
}

@Composable
fun FlashcardApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    val words = remember {
        mutableStateListOf(
            FlashcardWord(
                word = "apple",
                meaning = "quả táo",
                example = "I eat an apple."
            ),
            FlashcardWord(
                word = "book",
                meaning = "quyển sách",
                example = "This is my book."
            ),
            FlashcardWord(
                word = "study",
                meaning = "học tập",
                example = "I study English every day."
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                totalWords = words.size,
                masteredWords = words.count { it.isMastered },
                notMasteredWords = words.count { !it.isMastered },
                onAddWordClick = { currentScreen = Screen.ADD_WORD },
                onWordListClick = { currentScreen = Screen.WORD_LIST },
                onReviewClick = { currentScreen = Screen.REVIEW },
                onStatsClick = { currentScreen = Screen.STATS }
            )

            Screen.ADD_WORD -> AddWordScreen(
                onBackClick = { currentScreen = Screen.HOME },
                onSaveWord = { word, meaning, example ->
                    words.add(
                        FlashcardWord(
                            word = word,
                            meaning = meaning,
                            example = example
                        )
                    )
                    currentScreen = Screen.HOME
                }
            )

            Screen.WORD_LIST -> WordListScreen(
                words = words,
                onBackClick = { currentScreen = Screen.HOME },
                onDeleteWord = { word ->
                    words.remove(word)
                }
            )

            Screen.REVIEW -> ReviewScreen(
                words = words,
                onBackClick = { currentScreen = Screen.HOME },
                onUpdateWord = { index, updatedWord ->
                    words[index] = updatedWord
                }
            )

            Screen.STATS -> StatsScreen(
                words = words,
                onBackClick = { currentScreen = Screen.HOME }
            )
        }
    }
}

// ==========================================
// THÀNH PHẦN GIAO DIỆN CHUNG (UI COMPONENTS)
// ==========================================

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(IndigoPrimary, VioletSecondary)
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = IndigoPrimary,
            focusedLabelColor = IndigoPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        ),
        singleLine = true
    )
}

@Composable
fun FlipCard(
    word: FlashcardWord,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "cardFlipAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Mặt trước (FRONT)
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 38.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "💡 Chạm vào đây để lật thẻ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                // Mặt sau (BACK - Cần đảo ngược lại 180 độ để chữ không bị lật gương)
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .graphicsLayer { rotationY = 180f },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = word.meaning,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen,
                            fontSize = 28.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    if (word.example.isNotBlank()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(IndigoContainer.copy(alpha = 0.6f))
                                .padding(horizontal = 20.dp, vertical = 14.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Ví dụ minh họa",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = word.example,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontStyle = FontStyle.Italic
                                    ),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// MÀN HÌNH CHÍNH (HOME SCREEN)
// ==========================================

@Composable
fun HomeScreen(
    totalWords: Int,
    masteredWords: Int,
    notMasteredWords: Int,
    onAddWordClick: () -> Unit,
    onWordListClick: () -> Unit,
    onReviewClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Phần tiêu đề (Header)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Flashcard Pro ✨",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Làm chủ từ vựng mỗi ngày",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Thẻ tiến trình học tập
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tiến độ học tập",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tỷ lệ từ đã thuộc lòng",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    val percent = if (totalWords > 0) (masteredWords * 100 / totalWords) else 0
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = IndigoPrimary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                val progress = if (totalWords > 0) (masteredWords.toFloat() / totalWords.toFloat()) else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = EmeraldGreen,
                    trackColor = IndigoContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Đã thuộc: $masteredWords",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(RoseRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chưa thuộc: $notMasteredWords",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Grid Menu điều hướng
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nút Thêm từ mới
                HomeMenuCard(
                    title = "Thêm từ mới",
                    subtitle = "Tạo thẻ học",
                    icon = { Icon(Icons.Default.Add, contentDescription = null, tint = IndigoPrimary) },
                    onClick = onAddWordClick,
                    modifier = Modifier.weight(1f)
                )

                // Nút Danh sách từ
                HomeMenuCard(
                    title = "Bộ từ vựng",
                    subtitle = "$totalWords từ vựng",
                    icon = { Icon(Icons.Default.Star, contentDescription = null, tint = VioletSecondary) },
                    onClick = onWordListClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nút Thống kê học tập
                HomeMenuCard(
                    title = "Thống kê",
                    subtitle = "Xem hiệu suất",
                    icon = { Icon(Icons.Default.Info, contentDescription = null, tint = EmeraldGreen) },
                    onClick = onStatsClick,
                    modifier = Modifier.weight(1f)
                )

                // Box trống để giữ Grid đối xứng hoặc hiển thị thông tin nhanh
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💡 Mẹo nhỏ",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Học đều đặn mỗi ngày!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Nút chính: Bắt đầu ôn tập (Call To Action)
        GradientButton(
            text = "Bắt đầu ôn tập",
            onClick = onReviewClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White) }
        )
    }
}

@Composable
fun HomeMenuCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(IndigoContainer),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================
// MÀN HÌNH THÊM TỪ MỚI (ADD WORD SCREEN)
// ==========================================

@Composable
fun AddWordScreen(
    onBackClick: () -> Unit,
    onSaveWord: (String, String, String) -> Unit
) {
    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Thanh tiêu đề (App Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Thêm từ mới",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Các ô nhập liệu
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = word,
                onValueChange = {
                    word = it
                    errorMessage = ""
                },
                label = "Từ vựng",
                placeholder = "Ví dụ: Persistence",
                leadingIcon = { Icon(Icons.Default.Info, null, tint = IndigoPrimary) }
            )

            CustomTextField(
                value = meaning,
                onValueChange = {
                    meaning = it
                    errorMessage = ""
                },
                label = "Nghĩa tiếng Việt",
                placeholder = "Ví dụ: Sự kiên trì, bền bỉ",
                leadingIcon = { Icon(Icons.Default.Check, null, tint = EmeraldGreen) }
            )

            CustomTextField(
                value = example,
                onValueChange = { example = it },
                label = "Câu ví dụ minh họa",
                placeholder = "Ví dụ: Persistence pays off.",
                leadingIcon = { Icon(Icons.Default.Star, null, tint = VioletSecondary) }
            )

            if (errorMessage.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(RoseRed.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = RoseRed,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bản xem trước thẻ học trực tiếp (Live Card Preview)
            Text(
                text = "Xem trước thẻ trực tiếp (Bấm thử để lật)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            var isPreviewFlipped by remember { mutableStateOf(false) }
            FlipCard(
                word = FlashcardWord(
                    word = if (word.isBlank()) "Từ vựng" else word,
                    meaning = if (meaning.isBlank()) "Nghĩa của từ" else meaning,
                    example = if (example.isBlank()) "Ví dụ minh họa sẽ hiển thị ở đây..." else example
                ),
                isFlipped = isPreviewFlipped,
                onFlip = { isPreviewFlipped = !isPreviewFlipped },
                modifier = Modifier.height(180.dp)
            )
        }

        // Các nút điều khiển ở dưới cùng
        GradientButton(
            text = "Lưu thẻ học này",
            onClick = {
                if (word.isBlank() || meaning.isBlank()) {
                    errorMessage = "⚠️ Vui lòng nhập đầy đủ cả từ vựng và nghĩa tiếng Việt."
                } else {
                    onSaveWord(word.trim(), meaning.trim(), example.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ==========================================
// MÀN HÌNH DANH SÁCH TỪ VỰNG (WORD LIST SCREEN)
// ==========================================

@Composable
fun WordListScreen(
    words: List<FlashcardWord>,
    onBackClick: () -> Unit,
    onDeleteWord: (FlashcardWord) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Bộ lọc tìm kiếm từ vựng
    val filteredWords = words.filter {
        it.word.contains(searchQuery, ignoreCase = true) ||
        it.meaning.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Thanh tiêu đề
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Bộ từ vựng",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Ô tìm kiếm từ vựng hiện đại
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Tìm kiếm từ vựng, ý nghĩa...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = IndigoPrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IndigoPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        // Danh sách hiển thị
        if (filteredWords.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (words.isEmpty()) "Chưa có từ nào trong kho." else "Không tìm thấy từ vựng nào khớp 🔍",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredWords) { item ->
                    WordListItem(
                        word = item,
                        onDelete = { onDeleteWord(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun WordListItem(
    word: FlashcardWord,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                // Cột màu báo trạng thái trái thẻ
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (word.isMastered) EmeraldGreen else AmberYellow)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = word.word,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Badge hiển thị trạng thái đã thuộc / chưa thuộc
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (word.isMastered) EmeraldGreen.copy(alpha = 0.15f)
                                    else AmberYellow.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (word.isMastered) "Đã thuộc" else "Chưa thuộc",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (word.isMastered) EmeraldGreen else AmberYellow
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = word.meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (word.example.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ví dụ: ${word.example}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(RoseRed.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa từ",
                    tint = RoseRed
                )
            }
        }
    }
}

// ==========================================
// MÀN HÌNH ÔN TẬP (REVIEW SCREEN)
// ==========================================

@Composable
fun ReviewScreen(
    words: List<FlashcardWord>,
    onBackClick: () -> Unit,
    onUpdateWord: (Int, FlashcardWord) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    if (words.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chưa có từ vựng nào để ôn tập! 📚",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hãy thêm một vài từ vựng mới trước khi bắt đầu nhé.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Quay lại")
            }
        }
        return
    }

    val safeIndex = currentIndex.coerceIn(0, words.lastIndex)
    val currentWord = words[safeIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Thanh tiêu đề + Nút đóng
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ôn tập từ vựng 🧠",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Dừng ôn tập")
            }
        }

        // Tiến độ ôn tập bằng thanh Progress
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Thẻ học số ${safeIndex + 1}/${words.size}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${((safeIndex + 1) * 100 / words.size)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = IndigoPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            val progress = (safeIndex + 1).toFloat() / words.size.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = IndigoPrimary,
                trackColor = IndigoContainer
            )
        }

        // Thẻ lật 3D
        FlipCard(
            word = currentWord,
            isFlipped = isFlipped,
            onFlip = { isFlipped = !isFlipped }
        )

        // Các nút trả lời ôn tập
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bạn có nhớ từ vựng này không?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nút Chưa Thuộc (Màu Đỏ)
                Button(
                    onClick = {
                        val updatedWord = currentWord.copy(
                            isMastered = false,
                            wrongCount = currentWord.wrongCount + 1
                        )
                        onUpdateWord(safeIndex, updatedWord)

                        if (safeIndex < words.lastIndex) {
                            currentIndex++
                            isFlipped = false
                        } else {
                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoseRed.copy(alpha = 0.15f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = RoseRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chưa thuộc", color = RoseRed, fontWeight = FontWeight.Bold)
                    }
                }

                // Nút Đã Thuộc (Màu Xanh Lá)
                Button(
                    onClick = {
                        val updatedWord = currentWord.copy(
                            isMastered = true,
                            correctCount = currentWord.correctCount + 1
                        )
                        onUpdateWord(safeIndex, updatedWord)

                        if (safeIndex < words.lastIndex) {
                            currentIndex++
                            isFlipped = false
                        } else {
                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Đã thuộc", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// MÀN HÌNH THỐNG KÊ (STATS SCREEN)
// ==========================================

@Composable
fun StatsScreen(
    words: List<FlashcardWord>,
    onBackClick: () -> Unit
) {
    val totalWords = words.size
    val masteredWords = words.count { it.isMastered }
    val totalCorrect = words.sumOf { it.correctCount }
    val totalWrong = words.sumOf { it.wrongCount }
    val totalReviews = totalCorrect + totalWrong

    val rememberRate = if (totalReviews == 0) 0 else (totalCorrect * 100 / totalReviews)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Thanh tiêu đề
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Thống kê kết quả",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Biểu đồ vòng tròn hiển thị tỷ lệ nhớ
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                CircularProgressIndicator(
                    progress = { rememberRate.toFloat() / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = EmeraldGreen,
                    strokeWidth = 14.dp,
                    trackColor = IndigoContainer
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$rememberRate%",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tỷ lệ nhớ từ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mạng lưới các chỉ số Stats KPI Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItemCard(
                    title = "Tổng từ vựng",
                    value = "$totalWords",
                    icon = { Icon(Icons.Default.Info, null, tint = IndigoPrimary) },
                    modifier = Modifier.weight(1f)
                )
                StatItemCard(
                    title = "Đã thuộc lòng",
                    value = "$masteredWords",
                    icon = { Icon(Icons.Default.Star, null, tint = VioletSecondary) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItemCard(
                    title = "Đúng (Ôn tập)",
                    value = "$totalCorrect",
                    icon = { Icon(Icons.Default.Check, null, tint = EmeraldGreen) },
                    modifier = Modifier.weight(1f)
                )
                StatItemCard(
                    title = "Sai (Ôn tập)",
                    value = "$totalWrong",
                    icon = { Icon(Icons.Default.Close, null, tint = RoseRed) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nút quay lại
        GradientButton(
            text = "Quay lại Trang chủ",
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StatItemCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                icon()
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardAppPreview() {
    FlashcardTheme {
        FlashcardApp()
    }
}