package com.example.flashcard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.flashcard.model.Deck

@Entity(
    tableName = "decks",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ownerId")]
)
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val name: String,
    val description: String,
    val category: String,
    val language: String,
    val createdAt: Long
) {
    fun toModel() = Deck(id, ownerId, name, description, category, language, createdAt)
}

fun Deck.toEntity() = DeckEntity(
    id = id,
    ownerId = ownerId,
    name = name,
    description = description,
    category = category,
    language = language,
    createdAt = createdAt
)
