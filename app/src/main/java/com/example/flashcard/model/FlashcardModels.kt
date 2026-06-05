package com.example.flashcard.model

data class Deck(
    val id: Long = 0,
    val ownerName: String,
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

enum class Screen {
    LOGIN,
    REGISTER,
    HOME,
    DECK_DETAIL,
    ADD_CARD,
    REVIEW,
    STATS
}

enum class ReviewQuality {
    FORGOT,
    REMEMBERED,
    EASY
}

fun Flashcard.calculateNextReview(quality: ReviewQuality, now: Long): Flashcard {
    val days = when (quality) {
        ReviewQuality.FORGOT -> 1L
        ReviewQuality.REMEMBERED -> (correctCount + 2).coerceAtLeast(3).toLong()
        ReviewQuality.EASY -> ((correctCount + 1) * 7).coerceAtLeast(7).toLong()
    }
    return when (quality) {
        ReviewQuality.FORGOT -> this.copy(
            isMastered = false,
            wrongCount = wrongCount + 1,
            nextReviewAt = now + java.util.concurrent.TimeUnit.DAYS.toMillis(days),
            updatedAt = now
        )

        ReviewQuality.REMEMBERED,
        ReviewQuality.EASY -> this.copy(
            isMastered = true,
            correctCount = correctCount + 1,
            nextReviewAt = now + java.util.concurrent.TimeUnit.DAYS.toMillis(days),
            updatedAt = now
        )
    }
}

