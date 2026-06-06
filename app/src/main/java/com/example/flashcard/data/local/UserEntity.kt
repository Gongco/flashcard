package com.example.flashcard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val name: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)
