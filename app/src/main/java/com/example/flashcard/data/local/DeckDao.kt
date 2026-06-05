package com.example.flashcard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks WHERE ownerName = :ownerName ORDER BY createdAt DESC")
    fun observeDecksByOwner(ownerName: String): Flow<List<DeckEntity>>

    @Query("SELECT COUNT(*) FROM decks WHERE ownerName = :ownerName")
    suspend fun getDeckCountByOwner(ownerName: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(deck: DeckEntity): Long

    @Delete
    suspend fun delete(deck: DeckEntity)
}
