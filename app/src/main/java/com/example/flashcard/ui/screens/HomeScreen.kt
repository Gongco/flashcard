package com.example.flashcard.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.ui.components.CustomTextField
import com.example.flashcard.ui.components.GradientButton
import com.example.flashcard.ui.theme.EmeraldGreen
import com.example.flashcard.ui.theme.IndigoContainer
import com.example.flashcard.ui.theme.IndigoPrimary
import com.example.flashcard.ui.theme.RoseRed
import com.example.flashcard.ui.theme.VioletSecondary

@Composable
fun HomeScreen(
    userName: String,
    decks: List<Deck>,
    cards: List<Flashcard>,
    dueCards: List<Flashcard>,
    onAddDeck: (String, String, String, String) -> Unit,
    onOpenDeck: (Long) -> Unit,
    onReviewClick: () -> Unit,
    onStatsClick: () -> Unit,
    onLogout: () -> Unit
) {
    var showDeckDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Xin chào, $userName",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Hôm nay có ${dueCards.size} thẻ cần ôn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Đăng xuất", tint = RoseRed)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Bộ thẻ", decks.size.toString(), IndigoPrimary, Modifier.weight(1f))
            SummaryCard("Tổng thẻ", cards.size.toString(), VioletSecondary, Modifier.weight(1f))
            SummaryCard("Cần ôn", dueCards.size.toString(), EmeraldGreen, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GradientButton(
                text = "Ôn tập",
                onClick = onReviewClick,
                enabled = dueCards.isNotEmpty(),
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White) }
            )
            GradientButton(
                text = "Thống kê",
                onClick = onStatsClick,
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Default.Info, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bộ thẻ của bạn",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
                onClick = { showDeckDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(IndigoContainer)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tạo bộ thẻ", tint = IndigoPrimary)
            }
        }

        if (decks.isEmpty()) {
            EmptyState("Chưa có bộ thẻ nào. Hãy tạo bộ thẻ đầu tiên để bắt đầu học.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(decks, key = { it.id }) { deck ->
                    val count = cards.count { it.deckId == deck.id }
                    DeckItem(deck, count, onClick = { onOpenDeck(deck.id) })
                }
            }
        }
    }

    if (showDeckDialog) {
        DeckDialog(
            onDismiss = { showDeckDialog = false },
            onSave = { name, description, category, language ->
                onAddDeck(name, description, category, language)
                showDeckDialog = false
            }
        )
    }
}

@Composable
private fun SummaryCard(title: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(88.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = color)
        }
    }
}

@Composable
private fun DeckItem(deck: Deck, cardCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(IndigoPrimary)
                    .padding(horizontal = 3.dp)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deck.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = "${deck.category} • ${deck.language} • $cardCount thẻ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (deck.description.isNotBlank()) {
                    Text(deck.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun DeckDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo bộ thẻ mới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CustomTextField(name, { name = it }, "Tên bộ thẻ", "Ví dụ: Cấu trúc dữ liệu")
                CustomTextField(description, { description = it }, "Mô tả", "Nội dung ôn tập", singleLine = false, minLines = 2)
                CustomTextField(category, { category = it }, "Lĩnh vực", "Ví dụ: Lập trình")
                CustomTextField(language, { language = it }, "Ngôn ngữ", "Ví dụ: Tiếng Việt")
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, description, category, language) }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
