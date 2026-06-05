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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.ui.components.CustomTextField
import com.example.flashcard.ui.components.GradientButton
import com.example.flashcard.ui.theme.EmeraldGreen
import com.example.flashcard.ui.theme.FlashcardTheme
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
    onUpdateDeck: (Deck, String, String, String, String) -> Unit,
    onDeleteDeck: (Deck) -> Unit,
    onOpenDeck: (Long) -> Unit,
    onReviewClick: () -> Unit,
    onStatsClick: () -> Unit,
    onLogout: () -> Unit
) {
    var showDeckDialog by remember { mutableStateOf(false) }
    var editingDeck by remember { mutableStateOf<Deck?>(null) }
    var deletingDeck by remember { mutableStateOf<Deck?>(null) }
    var searchText by remember { mutableStateOf("") }

    val filteredDecks = decks.filter { deck ->
        val text = searchText.trim()
        text.isBlank() ||
            deck.name.contains(text, ignoreCase = true) ||
            deck.description.contains(text, ignoreCase = true) ||
            deck.category.contains(text, ignoreCase = true) ||
            deck.language.contains(text, ignoreCase = true)
    }

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

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard("Bộ thẻ", decks.size.toString(), IndigoPrimary, Modifier.weight(1f))
            SummaryCard("Tổng thẻ", cards.size.toString(), VioletSecondary, Modifier.weight(1f))
            SummaryCard("Cần ôn", dueCards.size.toString(), EmeraldGreen, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GradientButton(
                text = "Ôn tập",
                onClick = onReviewClick,
                enabled = dueCards.isNotEmpty(),
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
            )
            GradientButton(
                text = "Thống kê",
                onClick = onStatsClick,
                modifier = Modifier.weight(1f),
                icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
            )
        }

        CustomTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = "Tìm bộ thẻ",
            placeholder = "Nhập tên, lĩnh vực hoặc ngôn ngữ"
        )

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
        } else if (filteredDecks.isEmpty()) {
            EmptyState("Không tìm thấy bộ thẻ phù hợp.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredDecks, key = { it.id }) { deck ->
                    val count = cards.count { it.deckId == deck.id }
                    DeckItem(
                        deck = deck,
                        cardCount = count,
                        onClick = { onOpenDeck(deck.id) },
                        onEdit = { editingDeck = deck },
                        onDelete = { deletingDeck = deck }
                    )
                }
            }
        }
    }

    if (showDeckDialog) {
        DeckDialog(
            title = "Tạo bộ thẻ mới",
            onDismiss = { showDeckDialog = false },
            onSave = { name, description, category, language ->
                onAddDeck(name, description, category, language)
                showDeckDialog = false
            }
        )
    }

    editingDeck?.let { deck ->
        DeckDialog(
            title = "Sửa bộ thẻ",
            initialDeck = deck,
            onDismiss = { editingDeck = null },
            onSave = { name, description, category, language ->
                onUpdateDeck(deck, name, description, category, language)
                editingDeck = null
            }
        )
    }

    deletingDeck?.let { deck ->
        ConfirmDeleteDialog(
            title = "Xóa bộ thẻ?",
            message = "Toàn bộ thẻ trong \"${deck.name}\" cũng sẽ bị xóa.",
            confirmText = "Xóa",
            onDismiss = { deletingDeck = null },
            onConfirm = {
                onDeleteDeck(deck)
                deletingDeck = null
            }
        )
    }
}

@Composable
private fun SummaryCard(title: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(82.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = color, maxLines = 1)
        }
    }
}

@Composable
private fun DeckItem(
    deck: Deck,
    cardCount: Int,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
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
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa bộ thẻ", tint = IndigoPrimary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa bộ thẻ", tint = RoseRed)
            }
        }
    }
}

@Composable
private fun DeckDialog(
    title: String,
    initialDeck: Deck? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember(initialDeck) { mutableStateOf(initialDeck?.name ?: "") }
    var description by remember(initialDeck) { mutableStateOf(initialDeck?.description ?: "") }
    var category by remember(initialDeck) { mutableStateOf(initialDeck?.category ?: "") }
    var language by remember(initialDeck) { mutableStateOf(initialDeck?.language ?: "") }
    var error by remember(initialDeck) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CustomTextField(name, { name = it; error = "" }, "Tên bộ thẻ", "Ví dụ: Cấu trúc dữ liệu")
                CustomTextField(description, { description = it }, "Mô tả", "Nội dung ôn tập", singleLine = false, minLines = 2)
                CustomTextField(category, { category = it }, "Lĩnh vực", "Ví dụ: Lập trình")
                CustomTextField(language, { language = it }, "Ngôn ngữ", "Ví dụ: Tiếng Việt")
                if (error.isNotBlank()) {
                    Text(error, color = RoseRed, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        error = "Vui lòng nhập tên bộ thẻ."
                    } else {
                        onSave(name, description, category, language)
                    }
                }
            ) {
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
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    confirmText: String = "Xóa",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = RoseRed)
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val mockDecks = listOf(
        Deck(1, "Guest", "Từ vựng Tiếng Anh", "Các từ vựng cơ bản", "Ngoại ngữ", "Tiếng Anh"),
        Deck(2, "Guest", "Cấu trúc dữ liệu", "Giải thuật cơ bản", "Lập trình", "Tiếng Việt")
    )
    val mockCards = listOf(
        Flashcard(1, 1, "Hello", "Xin chào"),
        Flashcard(2, 1, "World", "Thế giới")
    )

    FlashcardTheme {
        HomeScreen(
            userName = "Người dùng",
            decks = mockDecks,
            cards = mockCards,
            dueCards = mockCards,
            onAddDeck = { _, _, _, _ -> },
            onUpdateDeck = { _, _, _, _, _ -> },
            onDeleteDeck = {},
            onOpenDeck = {},
            onReviewClick = {},
            onStatsClick = {},
            onLogout = {}
        )
    }
}
