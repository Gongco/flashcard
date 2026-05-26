package com.example.flashcard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewHistoryDao {
    @Query("SELECT * FROM review_history ORDER BY reviewedAt DESC")
    fun observeReviewHistory(): Flow<List<ReviewHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ReviewHistoryEntity): Long
}
