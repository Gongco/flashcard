package com.example.flashcard.model

data class Deck(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: String = "",
    val language: String = "Any",
    val createdAt: Long = System.currentTimeMillis()
)

data class Flashcard(
    val id: Long = 0,
    val deckId: Long,
    val frontText: String,
    val backText: String,
    val note: String = "",
    val isMastered: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val nextReviewAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ReviewHistory(
    val id: Long = 0,
    val cardId: Long,
    val deckId: Long,
    val isCorrect: Boolean,
    val reviewedAt: Long = System.currentTimeMillis()
)

enum class Screen {
    LOGIN,
    HOME,
    DECK_DETAIL,
    ADD_CARD,
    REVIEW,
    STATS
}
