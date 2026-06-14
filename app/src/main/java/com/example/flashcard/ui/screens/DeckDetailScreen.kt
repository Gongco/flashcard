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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.ui.components.CustomTextField
import com.example.flashcard.ui.components.GradientButton
import com.example.flashcard.ui.theme.EmeraldGreen
import com.example.flashcard.ui.theme.FlashcardTheme
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
    onUpdateDeck: (Deck, String, String, String, String) -> Unit,
    onDeleteDeck: (Deck) -> Unit,
    onUpdateCard: (Flashcard, String, String, String) -> Unit,
    onDeleteCard: (Flashcard) -> Unit
) {
    var editingDeck by remember { mutableStateOf(false) }
    var deletingDeck by remember { mutableStateOf(false) }
    var editingCard by remember { mutableStateOf<Flashcard?>(null) }
    var deletingCard by remember { mutableStateOf<Flashcard?>(null) }
    var searchText by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("all") }

    if (deck == null) {
        EmptyState("Không tìm thấy bộ thẻ.")
        return
    }

    val now = System.currentTimeMillis()
    val filteredCards = cards.filter { card ->
        val matchesSearch = searchText.isBlank() ||
            card.frontText.contains(searchText, ignoreCase = true) ||
            card.backText.contains(searchText, ignoreCase = true) ||
            card.note.contains(searchText, ignoreCase = true)

        val matchesFilter = when (filterMode) {
            "due" -> card.nextReviewAt <= now
            "mastered" -> card.isMastered
            else -> true
        }
        matchesSearch && matchesFilter
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
            IconButton(onClick = { editingDeck = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa bộ thẻ", tint = IndigoPrimary)
            }
            IconButton(onClick = { deletingDeck = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa bộ thẻ", tint = RoseRed)
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
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White) }
        )

        CustomTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = "Tìm thẻ",
            placeholder = "Nhập câu hỏi, câu trả lời hoặc ghi chú"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterButton("Tất cả", filterMode == "all", Modifier.weight(1f)) { filterMode = "all" }
            FilterButton("Cần ôn", filterMode == "due", Modifier.weight(1f)) { filterMode = "due" }
            FilterButton("Đã nhớ", filterMode == "mastered", Modifier.weight(1f)) { filterMode = "mastered" }
        }

        if (cards.isEmpty()) {
            EmptyState("Bộ này chưa có thẻ nào. Hãy thêm thẻ đầu tiên.")
        } else if (filteredCards.isEmpty()) {
            EmptyState("Không tìm thấy thẻ phù hợp.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCards, key = { it.id }) { card ->
                    CardRow(
                        card = card,
                        onEdit = { editingCard = card },
                        onDelete = { deletingCard = card }
                    )
                }
            }
        }
    }

    if (editingDeck) {
        DeckEditDialog(
            deck = deck,
            onDismiss = { editingDeck = false },
            onSave = { name, description, category, language ->
                onUpdateDeck(deck, name, description, category, language)
                editingDeck = false
            }
        )
    }

    if (deletingDeck) {
        ConfirmDeleteDialog(
            title = "Xóa bộ thẻ?",
            message = "Toàn bộ thẻ trong \"${deck.name}\" cũng sẽ bị xóa.",
            onDismiss = { deletingDeck = false },
            onConfirm = {
                onDeleteDeck(deck)
                deletingDeck = false
                onBackClick()
            }
        )
    }

    editingCard?.let { card ->
        CardEditDialog(
            card = card,
            onDismiss = { editingCard = null },
            onSave = { frontText, backText, note ->
                onUpdateCard(card, frontText, backText, note)
                editingCard = null
            }
        )
    }

    deletingCard?.let { card ->
        ConfirmDeleteDialog(
            title = "Xóa thẻ?",
            message = "Thẻ \"${card.frontText}\" sẽ bị xóa khỏi bộ này.",
            onDismiss = { deletingCard = null },
            onConfirm = {
                onDeleteCard(card)
                deletingCard = null
            }
        )
    }
}

@Composable
private fun FilterButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) IndigoPrimary else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun CardRow(card: Flashcard, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.frontText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.backText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                if (card.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.note,
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa thẻ", tint = IndigoPrimary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa thẻ", tint = RoseRed)
            }
        }
    }
}

@Composable
private fun DeckEditDialog(
    deck: Deck,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember(deck) { mutableStateOf(deck.name) }
    var description by remember(deck) { mutableStateOf(deck.description) }
    var category by remember(deck) { mutableStateOf(deck.category) }
    var language by remember(deck) { mutableStateOf(deck.language) }
    var error by remember(deck) { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Sửa bộ thẻ",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(name, { name = it; error = "" }, "Tên bộ thẻ", "Ví dụ: Cấu trúc dữ liệu")
                        CustomTextField(description, { description = it }, "Mô tả", "Nội dung ôn tập", singleLine = false, minLines = 2)
                        CustomTextField(category, { category = it }, "Lĩnh vực", "Ví dụ: Lập trình")
                        CustomTextField(language, { language = it }, "Ngôn ngữ", "Ví dụ: Tiếng Việt")
                        
                        if (error.isNotBlank()) {
                            Text(error, color = RoseRed, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Hủy")
                        }
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
                    }
                }
            }
        }
    }
}

@Composable
private fun CardEditDialog(
    card: Flashcard,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var front by remember(card) { mutableStateOf(card.frontText) }
    var back by remember(card) { mutableStateOf(card.backText) }
    var note by remember(card) { mutableStateOf(card.note) }
    var error by remember(card) { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Sửa thẻ",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(front, { front = it; error = "" }, "Mặt trước", "Câu hỏi", singleLine = false, minLines = 2)
                        CustomTextField(back, { back = it; error = "" }, "Mặt sau", "Câu trả lời", singleLine = false, minLines = 3)
                        CustomTextField(note, { note = it }, "Ghi chú", "Mẹo nhớ hoặc nguồn tài liệu", singleLine = false, minLines = 2)
                        
                        if (error.isNotBlank()) {
                            Text(error, color = RoseRed, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Hủy")
                        }
                        TextButton(
                            onClick = {
                                if (front.isBlank() || back.isBlank()) {
                                    error = "Vui lòng nhập cả mặt trước và mặt sau."
                                } else {
                                    onSave(front, back, note)
                                }
                            }
                        ) {
                            Text("Lưu")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeckDetailScreenPreview() {
    val mockDeck = Deck(
        id = 1L,
        ownerId = 1L,
        name = "Tiếng Anh chuyên ngành",
        description = "Từ vựng về CNTT",
        category = "Ngoại ngữ",
        language = "Tiếng Anh"
    )
    val mockCards = listOf(
        Flashcard(
            id = 1L,
            deckId = 1L,
            frontText = "Database",
            backText = "Cơ sở dữ liệu"
        ),
        Flashcard(
            id = 2L,
            deckId = 1L,
            frontText = "Algorithm",
            backText = "Giải thuật",
            note = "Rất quan trọng"
        )
    )

    FlashcardTheme {
        DeckDetailScreen(
            deck = mockDeck,
            cards = mockCards,
            onBackClick = {},
            onAddCardClick = {},
            onReviewClick = {},
            onUpdateDeck = { _, _, _, _, _ -> },
            onDeleteDeck = {},
            onUpdateCard = { _, _, _, _ -> },
            onDeleteCard = {}
        )
    }
}
