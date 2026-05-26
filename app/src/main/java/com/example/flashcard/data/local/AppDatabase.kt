package com.example.flashcard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [DeckEntity::class, FlashcardEntity::class, ReviewHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun reviewHistoryDao(): ReviewHistoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS review_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        cardId INTEGER NOT NULL,
                        deckId INTEGER NOT NULL,
                        isCorrect INTEGER NOT NULL,
                        reviewedAt INTEGER NOT NULL,
                        FOREIGN KEY(cardId) REFERENCES flashcards(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(deckId) REFERENCES decks(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_review_history_cardId ON review_history(cardId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_review_history_deckId ON review_history(deckId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_review_history_reviewedAt ON review_history(reviewedAt)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "learnflash.db"
                )
                    .addMigrations(migration1To2)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
