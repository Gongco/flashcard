package com.example.flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flashcard.model.Flashcard
import com.example.flashcard.ui.components.FlipCard
import com.example.flashcard.ui.theme.AmberYellow
import com.example.flashcard.ui.theme.EmeraldGreen
import com.example.flashcard.ui.theme.IndigoContainer
import com.example.flashcard.ui.theme.IndigoPrimary
import com.example.flashcard.ui.theme.RoseRed
import com.example.flashcard.viewmodel.ReviewQuality

@Composable
fun ReviewScreen(
    cards: List<Flashcard>,
    title: String,
    onBackClick: () -> Unit,
    onReview: (Flashcard, ReviewQuality) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                Text(
                    text = "Thẻ ${safeIndex + 1}/${cards.size}",
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
            progress = { (safeIndex + 1).toFloat() / cards.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = IndigoPrimary,
            trackColor = IndigoContainer
        )

        FlipCard(
            card = currentCard,
            isFlipped = isFlipped,
            onFlip = { isFlipped = !isFlipped }
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    nextCard(safeIndex, cards.lastIndex, onBackClick) { currentIndex++; isFlipped = false }
                }
                ReviewButton("Nhớ", AmberYellow, Modifier.weight(1f)) {
                    onReview(currentCard, ReviewQuality.REMEMBERED)
                    nextCard(safeIndex, cards.lastIndex, onBackClick) { currentIndex++; isFlipped = false }
                }
                ReviewButton("Rất nhớ", EmeraldGreen, Modifier.weight(1f)) {
                    onReview(currentCard, ReviewQuality.EASY)
                    nextCard(safeIndex, cards.lastIndex, onBackClick) { currentIndex++; isFlipped = false }
                }
            }
        }
    }
}

private fun nextCard(index: Int, lastIndex: Int, onDone: () -> Unit, onNext: () -> Unit) {
    if (index < lastIndex) onNext() else onDone()
}

@Composable
private fun ReviewButton(
    text: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.16f))
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold)
    }
}
