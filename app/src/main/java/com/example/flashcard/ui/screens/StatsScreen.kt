package com.example.flashcard.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.ui.components.GradientButton
import com.example.flashcard.ui.theme.EmeraldGreen
import com.example.flashcard.ui.theme.FlashcardTheme
import com.example.flashcard.ui.theme.IndigoContainer
import com.example.flashcard.ui.theme.IndigoPrimary
import com.example.flashcard.ui.theme.RoseRed
import com.example.flashcard.ui.theme.VioletSecondary

@Composable
fun StatsScreen(
    decks: List<Deck>,
    cards: List<Flashcard>,
    dueCards: List<Flashcard>,
    onBackClick: () -> Unit
) {
    val totalCorrect = cards.sumOf { it.correctCount }
    val totalWrong = cards.sumOf { it.wrongCount }
    val totalReviews = totalCorrect + totalWrong
    val successRate = if (totalReviews == 0) 0 else totalCorrect * 100 / totalReviews
    val masteredCards = cards.count { it.isMastered }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Thống kê học tập",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(170.dp)) {
                    CircularProgressIndicator(
                        progress = { successRate / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = EmeraldGreen,
                        strokeWidth = 14.dp,
                        trackColor = IndigoContainer
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$successRate%",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 36.sp)
                        )
                        Text("Tỉ lệ nhớ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Bộ thẻ", decks.size.toString(), IndigoPrimary, Modifier.weight(1f))
                    StatCard("Tổng thẻ", cards.size.toString(), VioletSecondary, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Cần ôn", dueCards.size.toString(), EmeraldGreen, Modifier.weight(1f))
                    StatCard("Đã nhớ", masteredCards.toString(), IndigoPrimary, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Đúng", totalCorrect.toString(), EmeraldGreen, Modifier.weight(1f))
                    StatCard("Sai", totalWrong.toString(), RoseRed, Modifier.weight(1f))
                }
            }

            Text(
                text = "Tiến độ từng bộ",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            if (decks.isEmpty()) {
                EmptyState("Chưa có bộ thẻ để thống kê.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    decks.forEach { deck ->
                        val deckCards = cards.filter { it.deckId == deck.id }
                        val deckDue = dueCards.count { it.deckId == deck.id }
                        DeckProgressCard(deck, deckCards, deckDue)
                    }
                }
            }
        }

        GradientButton(text = "Quay lại", onClick = onBackClick, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(88.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = color)
        }
    }
}

@Composable
private fun DeckProgressCard(deck: Deck, cards: List<Flashcard>, dueCount: Int) {
    val masteredCount = cards.count { it.isMastered }
    val progress = if (cards.isEmpty()) 0f else masteredCount.toFloat() / cards.size.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(deck.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text("$masteredCount/${cards.size} thẻ", style = MaterialTheme.typography.bodySmall)
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = IndigoPrimary,
                trackColor = IndigoContainer
            )
            Text(
                text = "Cần ôn: $dueCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatsScreenPreview() {
    val mockDecks = listOf(
        Deck(
            id = 1L,
            ownerId = 1L,
            name = "Từ vựng IT",
            description = "",
            category = "Lập trình",
            language = "Tiếng Việt"
        )
    )
    val mockCards = listOf(
        Flashcard(
            id = 1L,
            deckId = 1L,
            frontText = "Variable",
            backText = "Biến",
            correctCount = 10,
            wrongCount = 2
        ),
        Flashcard(
            id = 2L,
            deckId = 1L,
            frontText = "Function",
            backText = "Hàm",
            correctCount = 5,
            wrongCount = 5
        ),
        Flashcard(
            id = 3L,
            deckId = 1L,
            frontText = "Class",
            backText = "Lớp",
            correctCount = 8,
            wrongCount = 0,
            isMastered = true
        )
    )

    FlashcardTheme {
        StatsScreen(
            decks = mockDecks,
            cards = mockCards,
            dueCards = mockCards.take(1),
            onBackClick = {}
        )
    }
}
