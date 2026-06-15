package com.example.flashcard

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.flashcard.ui.theme.FlashcardTheme
import com.example.flashcard.viewmodel.FlashcardViewModel

class MainActivity : ComponentActivity() {
    private companion object {
        const val LIFECYCLE_TAG = "LearnFlashLifecycle"
    }

    private val viewModel: FlashcardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LIFECYCLE_TAG, "onCreate")
        setContent {
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDark = viewModel.isDarkTheme ?: systemDark
            FlashcardTheme(darkTheme = isDark) {
                FlashcardApp(viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(LIFECYCLE_TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE_TAG, "onResume")
    }

    override fun onPause() {
        Log.d(LIFECYCLE_TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(LIFECYCLE_TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(LIFECYCLE_TAG, "onDestroy")
        super.onDestroy()
    }
}
