package com.example.flashcard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flashcard.model.Deck

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val category: String,
    val language: String,
    val createdAt: Long
) {
    fun toModel() = Deck(id, name, description, category, language, createdAt)
}

fun Deck.toEntity() = DeckEntity(
    id = id,
    name = name,
    description = description,
    category = category,
    language = language,
    createdAt = createdAt
)
