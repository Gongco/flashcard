package com.example.flashcard.data.repository

import com.example.flashcard.data.local.DeckDao
import com.example.flashcard.data.local.DeckEntity
import com.example.flashcard.data.local.FlashcardDao
import com.example.flashcard.data.local.FlashcardEntity
import com.example.flashcard.data.local.toEntity
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlashcardRepository(
    private val deckDao: DeckDao,
    private val flashcardDao: FlashcardDao
) {
    val decks: Flow<List<Deck>> = deckDao.observeDecks().map { decks ->
        decks.map(DeckEntity::toModel)
    }

    val cards: Flow<List<Flashcard>> = flashcardDao.observeCards().map { cards ->
        cards.map(FlashcardEntity::toModel)
    }

    fun cardsByDeck(deckId: Long): Flow<List<Flashcard>> {
        return flashcardDao.observeCardsByDeck(deckId).map { cards ->
            cards.map(FlashcardEntity::toModel)
        }
    }

    suspend fun saveDeck(deck: Deck): Long = deckDao.upsert(deck.toEntity())

    suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck.toEntity())

    suspend fun addCard(card: Flashcard): Long = flashcardDao.insert(card.toEntity())

    suspend fun updateCard(card: Flashcard) = flashcardDao.update(card.toEntity())

    suspend fun deleteCard(card: Flashcard) = flashcardDao.delete(card.toEntity())
}
