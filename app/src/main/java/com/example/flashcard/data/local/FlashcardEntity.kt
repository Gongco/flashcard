package com.example.flashcard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.flashcard.model.Flashcard

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId")]
)
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val frontText: String,
    val backText: String,
    val note: String,
    val isMastered: Boolean,
    val correctCount: Int,
    val wrongCount: Int,
    val nextReviewAt: Long,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toModel() = Flashcard(
        id = id,
        deckId = deckId,
        frontText = frontText,
        backText = backText,
        note = note,
        isMastered = isMastered,
        correctCount = correctCount,
        wrongCount = wrongCount,
        nextReviewAt = nextReviewAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Flashcard.toEntity() = FlashcardEntity(
    id = id,
    deckId = deckId,
    frontText = frontText,
    backText = backText,
    note = note,
    isMastered = isMastered,
    correctCount = correctCount,
    wrongCount = wrongCount,
    nextReviewAt = nextReviewAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)
