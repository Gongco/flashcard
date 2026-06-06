package com.example.flashcard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flashcard.model.Deck

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerName: String,
    val name: String,
    val description: String,
    val category: String,
    val language: String,
    val createdAt: Long
) {
    fun toModel() = Deck(id, ownerName, name, description, category, language, createdAt)
}

fun Deck.toEntity() = DeckEntity(
    id = id,
    ownerName = ownerName,
    name = name,
    description = description,
    category = category,
    language = language,
    createdAt = createdAt
)
