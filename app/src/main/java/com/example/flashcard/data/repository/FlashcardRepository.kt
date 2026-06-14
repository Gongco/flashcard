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
import com.example.flashcard.util.PasswordUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlashcardRepository(
    private val userDao: UserDao,
    private val deckDao: DeckDao,
    private val flashcardDao: FlashcardDao
) {
    /**
     * Tìm user theo tên.
     */
    suspend fun findUser(name: String): UserEntity? = userDao.findUser(name)

    /**
     * Đăng ký tài khoản mới.
     * Mật khẩu được hash bằng SHA-256 trước khi lưu vào CSDL.
     * @return ID của user vừa tạo, hoặc -1 nếu tên đã tồn tại
     */
    suspend fun register(name: String, password: String): Long {
        val user = userDao.findUser(name)
        if (user != null) {
            return -1L
        }
        val hashedPassword = PasswordUtils.hashPassword(password)
        return userDao.addUser(UserEntity(name = name, password = hashedPassword))
    }

    /**
     * Xác minh mật khẩu bằng cách hash và so sánh với hash đã lưu.
     * @return true nếu mật khẩu đúng
     */
    suspend fun verifyPassword(user: UserEntity, plainPassword: String): Boolean {
        return PasswordUtils.verifyPassword(plainPassword, user.password)
    }

    fun decksByOwner(ownerId: Long): Flow<List<Deck>> = deckDao.observeDecksByOwner(ownerId).map { decks ->
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

    suspend fun getDeckCountByOwner(ownerId: Long): Int = deckDao.getDeckCountByOwner(ownerId)

    suspend fun deleteDeck(deck: Deck) = deckDao.delete(deck.toEntity())

    suspend fun addCard(card: Flashcard): Long = flashcardDao.insert(card.toEntity())

    suspend fun updateCard(card: Flashcard) = flashcardDao.update(card.toEntity())

    suspend fun deleteCard(card: Flashcard) = flashcardDao.delete(card.toEntity())
}
