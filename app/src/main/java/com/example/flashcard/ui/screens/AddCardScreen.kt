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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.flashcard.ui.components.CustomTextField
import com.example.flashcard.ui.components.GradientButton
import com.example.flashcard.ui.theme.RoseRed

@Composable
fun AddCardScreen(
    onBackClick: () -> Unit,
    onSaveCard: (String, String, String) -> Unit
) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
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
                text = "Thêm thẻ học",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CustomTextField(
                value = front,
                onValueChange = {
                    front = it
                    error = ""
                },
                label = "Mặt trước",
                placeholder = "Câu hỏi, khái niệm, thuật ngữ...",
                singleLine = false,
                minLines = 3
            )
            CustomTextField(
                value = back,
                onValueChange = {
                    back = it
                    error = ""
                },
                label = "Mặt sau",
                placeholder = "Câu trả lời, định nghĩa, lời giải...",
                singleLine = false,
                minLines = 4
            )
            CustomTextField(
                value = note,
                onValueChange = { note = it },
                label = "Ghi chú",
                placeholder = "Ví dụ, mẹo nhớ, nguồn tài liệu...",
                singleLine = false,
                minLines = 3
            )
            if (error.isNotBlank()) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = RoseRed
                )
            }
        }

        GradientButton(
            text = "Lưu thẻ",
            onClick = {
                if (front.isBlank() || back.isBlank()) {
                    error = "Vui lòng nhập cả mặt trước và mặt sau của thẻ."
                } else {
                    onSaveCard(front, back, note)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
