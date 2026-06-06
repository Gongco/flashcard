package com.example.flashcard.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.flashcard.R
import com.example.flashcard.model.Flashcard
import com.example.flashcard.ui.components.FlipCard
import com.example.flashcard.ui.components.GradientButton
import com.example.flashcard.ui.theme.FlashcardTheme
import com.example.flashcard.ui.theme.AmberYellow
import com.example.flashcard.ui.theme.EmeraldGreen
import com.example.flashcard.ui.theme.IndigoContainer
import com.example.flashcard.ui.theme.IndigoPrimary
import com.example.flashcard.ui.theme.RoseRed
import com.example.flashcard.model.ReviewQuality

@Composable
fun ReviewScreen(
    cards: List<Flashcard>,
    title: String,
    onBackClick: () -> Unit,
    onStatsClick: () -> Unit,
    onReview: (Flashcard, ReviewQuality) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    LaunchedEffect(cards.size) {
        if (currentIndex > cards.lastIndex) currentIndex = 0
    }

    if (cards.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Không có thẻ nào cần ôn lúc này.",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(onClick = onBackClick, shape = RoundedCornerShape(14.dp)) {
                Text("Quay lại")
            }
        }
        return
    }

    val safeIndex = currentIndex.coerceIn(0, cards.lastIndex)
    val currentCard = cards[safeIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                Text(
                    text = if (isFinished) "Hoàn thành!" else "Thẻ ${safeIndex + 1}/${cards.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Dừng ôn tập")
            }
        }

        LinearProgressIndicator(
            progress = { if (isFinished) 1f else (safeIndex + 1).toFloat() / cards.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = IndigoPrimary,
            trackColor = IndigoContainer
        )

        if (!isFinished) {
            // Study State
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FlipCard(
                    card = currentCard,
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Bạn nhớ nội dung này ở mức nào?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReviewButton("Chưa nhớ", RoseRed, Modifier.weight(1f)) {
                        onReview(currentCard, ReviewQuality.FORGOT)
                        if (safeIndex < cards.lastIndex) {
                            currentIndex++
                            isFlipped = false
                        } else {
                            isFinished = true
                            isFlipped = false
                        }
                    }
                    ReviewButton("Nhớ", AmberYellow, Modifier.weight(1f)) {
                        onReview(currentCard, ReviewQuality.REMEMBERED)
                        if (safeIndex < cards.lastIndex) {
                            currentIndex++
                            isFlipped = false
                        } else {
                            isFinished = true
                            isFlipped = false
                        }
                    }
                    ReviewButton("Rất nhớ", EmeraldGreen, Modifier.weight(1f)) {
                        onReview(currentCard, ReviewQuality.EASY)
                        if (safeIndex < cards.lastIndex) {
                            currentIndex++
                            isFlipped = false
                        } else {
                            isFinished = true
                            isFlipped = false
                        }
                    }
                }
            }
        } else {
            // End State (Matching the image)
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                EndFlipCard(
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped }
                )
            }

            GradientButton(
                text = "Check thống kê",
                onClick = onStatsClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ReviewButton(
    text: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    // Solid background button styled like GradientButton
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun EndFlipCard(isFlipped: Boolean, onFlip: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "endCardFlip"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Image(
                    painter = painterResource(id = R.drawable.peak),
                    contentDescription = "Peak",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "This is the end",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        fontSize = 32.sp
                    ),
                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReviewScreenPreview() {
    val mockCards = listOf(
        Flashcard(1, 1, "Hello", "Xin chào"),
        Flashcard(2, 1, "World", "Thế giới"),
        Flashcard(3, 1, "Computer", "Máy tính")
    )

    FlashcardTheme {
        ReviewScreen(
            cards = mockCards,
            title = "Ôn tập: Tiếng Anh",
            onBackClick = {},
            onStatsClick = {},
            onReview = { _, _ -> }
        )
    }
}
