package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.flashcard.ui.theme.FlashcardTheme
import com.example.flashcard.viewmodel.FlashcardViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FlashcardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashcardTheme {
                FlashcardApp(viewModel)
            }
        }
    }
}
