package com.example.flashcard.data.repository

import com.example.flashcard.data.local.DeckDao
import com.example.flashcard.data.local.DeckEntity
import com.example.flashcard.data.local.FlashcardDao
import com.example.flashcard.data.local.FlashcardEntity
import com.example.flashcard.data.local.UserDao
import com.example.flashcard.data.local.UserEntity
import com.example.flashcard.data.local.toEntity
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlashcardRepository(
    private val userDao: UserDao,
    private val deckDao: DeckDao,
    private val flashcardDao: FlashcardDao
) {
    suspend fun loginOrRegister(name: String, password: String): Boolean {
        val user = userDao.findUser(name)
        if (user == null) {
            userDao.addUser(UserEntity(name = name, password = password))
            return true
        }
        return user.password == password
    }

    suspend fun findUser(name: String): UserEntity? = userDao.findUser(name)

    suspend fun register(name: String, password: String): Boolean {
        val user = userDao.findUser(name)
        if (user != null) {
            return false
        }
        userDao.addUser(UserEntity(name = name, password = password))
        return true
    }

    fun decksByOwner(ownerName: String): Flow<List<Deck>> = deckDao.observeDecksByOwner(ownerName).map { decks ->
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

    suspend fun getDeckCountByOwner(ownerName: String): Int = deckDao.getDeckCountByOwner(ownerName)

    suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck.toEntity())

    suspend fun addCard(card: Flashcard): Long = flashcardDao.insert(card.toEntity())

    suspend fun updateCard(card: Flashcard) = flashcardDao.update(card.toEntity())

    suspend fun deleteCard(card: Flashcard) = flashcardDao.delete(card.toEntity())
}
