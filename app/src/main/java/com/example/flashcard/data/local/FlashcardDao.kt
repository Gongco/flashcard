package com.example.flashcard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY updatedAt DESC")
    fun observeCards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY updatedAt DESC")
    fun observeCardsByDeck(deckId: Long): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: FlashcardEntity): Long

    @Update
    suspend fun update(card: FlashcardEntity)

    @Delete
    suspend fun delete(card: FlashcardEntity)
}
