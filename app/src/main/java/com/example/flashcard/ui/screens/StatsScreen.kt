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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.ReviewHistory
import com.example.flashcard.ui.components.GradientButton
import com.example.flashcard.ui.theme.EmeraldGreen
import com.example.flashcard.ui.theme.IndigoContainer
import com.example.flashcard.ui.theme.IndigoPrimary
import com.example.flashcard.ui.theme.RoseRed
import com.example.flashcard.ui.theme.VioletSecondary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    decks: List<Deck>,
    cards: List<Flashcard>,
    reviewHistory: List<ReviewHistory>,
    dueCards: List<Flashcard>,
    onBackClick: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf(StatsPeriod.DAY) }
    val periodStart = remember(selectedPeriod) { selectedPeriod.startMillis() }
    val filteredHistory = reviewHistory.filter { it.reviewedAt >= periodStart }
    val totalCorrect = filteredHistory.count { it.isCorrect }
    val totalWrong = filteredHistory.count { !it.isCorrect }
    val totalReviews = totalCorrect + totalWrong
    val successRate = if (totalReviews == 0) 0 else totalCorrect * 100 / totalReviews

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

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            StatsPeriod.entries.forEachIndexed { index, period ->
                SegmentedButton(
                    selected = selectedPeriod == period,
                    onClick = { selectedPeriod = period },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = StatsPeriod.entries.size
                    ),
                    label = { Text(period.label) }
                )
            }
        }

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
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp
                        )
                    )
                    Text(
                        text = "Tỉ lệ nhớ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                StatCard("Lượt ôn", totalReviews.toString(), IndigoPrimary, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Đúng", totalCorrect.toString(), EmeraldGreen, Modifier.weight(1f))
                StatCard("Sai", totalWrong.toString(), RoseRed, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        GradientButton(text = "Quay lại", onClick = onBackClick, modifier = Modifier.fillMaxWidth())
    }
}

private enum class StatsPeriod(val label: String) {
    DAY("Ngày"),
    WEEK("Tuần"),
    MONTH("Tháng");

    fun startMillis(): Long {
        val calendar = Calendar.getInstance()
        when (this) {
            DAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            WEEK -> {
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }
        return calendar.timeInMillis
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
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
