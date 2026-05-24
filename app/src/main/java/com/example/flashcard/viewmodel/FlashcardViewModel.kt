package com.example.flashcard.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.data.local.AppDatabase
import com.example.flashcard.data.repository.FlashcardRepository
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = FlashcardRepository(database.deckDao(), database.flashcardDao())

    val decks: StateFlow<List<Deck>> = repository.decks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val cards: StateFlow<List<Flashcard>> = repository.cards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val dueCards: StateFlow<List<Flashcard>> = cards.combine(decks) { cardList, _ ->
        val now = System.currentTimeMillis()
        cardList.filter { it.nextReviewAt <= now }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    var currentScreen by mutableStateOf(Screen.LOGIN)
        private set

    var currentUserName by mutableStateOf("")
        private set

    var selectedDeckId by mutableLongStateOf(0L)
        private set

    var reviewSelectedDeckOnly by mutableStateOf(false)
        private set

    fun login(name: String, password: String): Boolean {
        if (name.isBlank() || password.isBlank()) return false
        currentUserName = name.trim()
        currentScreen = Screen.HOME
        seedStarterDeckIfNeeded()
        return true
    }

    fun logout() {
        currentUserName = ""
        selectedDeckId = 0L
        currentScreen = Screen.LOGIN
    }

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun openDeck(deckId: Long) {
        selectedDeckId = deckId
        currentScreen = Screen.DECK_DETAIL
    }

    fun startDueReview() {
        reviewSelectedDeckOnly = false
        currentScreen = Screen.REVIEW
    }

    fun startDeckReview() {
        reviewSelectedDeckOnly = true
        currentScreen = Screen.REVIEW
    }

    fun addDeck(name: String, description: String, category: String, language: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val deckId = repository.saveDeck(
                Deck(
                    name = name.trim(),
                    description = description.trim(),
                    category = category.trim().ifBlank { "General" },
                    language = language.trim().ifBlank { "Any" }
                )
            )
            selectedDeckId = deckId
            currentScreen = Screen.DECK_DETAIL
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
            if (selectedDeckId == deck.id) {
                selectedDeckId = 0L
                currentScreen = Screen.HOME
            }
        }
    }

    fun addCard(frontText: String, backText: String, note: String) {
        val deckId = selectedDeckId
        if (deckId == 0L || frontText.isBlank() || backText.isBlank()) return
        viewModelScope.launch {
            repository.addCard(
                Flashcard(
                    deckId = deckId,
                    frontText = frontText.trim(),
                    backText = backText.trim(),
                    note = note.trim()
                )
            )
            currentScreen = Screen.DECK_DETAIL
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    fun recordReview(card: Flashcard, quality: ReviewQuality) {
        val now = System.currentTimeMillis()
        val days = when (quality) {
            ReviewQuality.FORGOT -> 1L
            ReviewQuality.REMEMBERED -> 3L
            ReviewQuality.EASY -> 7L
        }
        val updated = when (quality) {
            ReviewQuality.FORGOT -> card.copy(
                isMastered = false,
                wrongCount = card.wrongCount + 1,
                nextReviewAt = now + TimeUnit.DAYS.toMillis(days),
                updatedAt = now
            )

            ReviewQuality.REMEMBERED,
            ReviewQuality.EASY -> card.copy(
                isMastered = true,
                correctCount = card.correctCount + 1,
                nextReviewAt = now + TimeUnit.DAYS.toMillis(days),
                updatedAt = now
            )
        }
        viewModelScope.launch {
            repository.updateCard(updated)
        }
    }

    private fun seedStarterDeckIfNeeded() {
        viewModelScope.launch {
            if (decks.value.isNotEmpty()) return@launch
            val deckId = repository.saveDeck(
                Deck(
                    name = "Kotlin Basics",
                    description = "Starter cards for general study",
                    category = "Programming",
                    language = "English"
                )
            )
            repository.addCard(
                Flashcard(
                    deckId = deckId,
                    frontText = "What is a data class?",
                    backText = "A Kotlin class designed to hold data and generate common functions automatically.",
                    note = "Examples: equals, hashCode, toString, copy."
                )
            )
            repository.addCard(
                Flashcard(
                    deckId = deckId,
                    frontText = "ViewModel is used for what?",
                    backText = "It stores UI state and survives configuration changes.",
                    note = "Useful in Jetpack Compose apps."
                )
            )
        }
    }
}

enum class ReviewQuality {
    FORGOT,
    REMEMBERED,
    EASY
}
