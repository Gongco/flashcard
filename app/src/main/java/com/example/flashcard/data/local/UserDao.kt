package com.example.flashcard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun findUser(name: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUser(user: UserEntity)
}
