package com.example.flashcard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    fun observeDecks(): Flow<List<DeckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(deck: DeckEntity): Long

    @Delete
    suspend fun delete(deck: DeckEntity)
}
