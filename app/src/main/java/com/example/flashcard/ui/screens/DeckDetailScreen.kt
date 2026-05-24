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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.ui.components.GradientButton
import com.example.flashcard.ui.theme.IndigoContainer
import com.example.flashcard.ui.theme.IndigoPrimary
import com.example.flashcard.ui.theme.RoseRed

@Composable
fun DeckDetailScreen(
    deck: Deck?,
    cards: List<Flashcard>,
    onBackClick: () -> Unit,
    onAddCardClick: () -> Unit,
    onReviewClick: () -> Unit,
    onDeleteCard: (Flashcard) -> Unit
) {
    if (deck == null) {
        EmptyState("Không tìm thấy bộ thẻ.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deck.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                Text(
                    text = "${deck.category} • ${deck.language} • ${cards.size} thẻ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onAddCardClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(IndigoContainer)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm thẻ", tint = IndigoPrimary)
            }
        }

        if (deck.description.isNotBlank()) {
            Text(deck.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        GradientButton(
            text = "Ôn bộ thẻ này",
            onClick = onReviewClick,
            enabled = cards.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White) }
        )

        if (cards.isEmpty()) {
            EmptyState("Bộ này chưa có thẻ nào. Hãy thêm thẻ đầu tiên.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    CardRow(card = card, onDelete = { onDeleteCard(card) })
                }
            }
        }
    }
}

@Composable
private fun CardRow(card: Flashcard, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(card.frontText, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(card.backText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (card.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.note,
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(RoseRed.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa thẻ", tint = RoseRed)
            }
        }
    }
}
