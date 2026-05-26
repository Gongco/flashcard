package com.example.flashcard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.flashcard.model.ReviewHistory

@Entity(
    tableName = "review_history",
    foreignKeys = [
        ForeignKey(
            entity = FlashcardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cardId"), Index("deckId"), Index("reviewedAt")]
)
data class ReviewHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val deckId: Long,
    val isCorrect: Boolean,
    val reviewedAt: Long
) {
    fun toModel() = ReviewHistory(
        id = id,
        cardId = cardId,
        deckId = deckId,
        isCorrect = isCorrect,
        reviewedAt = reviewedAt
    )
}

fun ReviewHistory.toEntity() = ReviewHistoryEntity(
    id = id,
    cardId = cardId,
    deckId = deckId,
    isCorrect = isCorrect,
    reviewedAt = reviewedAt
)
