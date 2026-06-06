package com.example.flashcard

import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.ReviewQuality
import com.example.flashcard.model.calculateNextReview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class FlashcardSrsTest {

    @Test
    fun testCalculateNextReview_whenForgot_resetsMasteredAndSetsNextReviewTo1Day() {
        // 1. Arrange: Chuẩn bị thẻ ban đầu có isMastered = true và correctCount = 4
        val now = 1000000000L
        val card = Flashcard(
            id = 1,
            deckId = 101,
            frontText = "Front",
            backText = "Back",
            correctCount = 4,
            wrongCount = 1,
            isMastered = true,
            createdAt = now,
            updatedAt = now
        )

        // 2. Act: Thực hiện ôn tập với chất lượng FORGOT (Quên)
        val updatedCard = card.calculateNextReview(ReviewQuality.FORGOT, now)

        // 3. Assert: Kiểm tra xem các giá trị có thay đổi đúng như mong đợi không
        assertFalse(updatedCard.isMastered)
        assertEquals(card.correctCount, updatedCard.correctCount) // Không đổi
        assertEquals(2, updatedCard.wrongCount) // wrongCount tăng 1
        
        // Hẹn lịch sau 1 ngày (1L)
        val expectedNextReview = now + TimeUnit.DAYS.toMillis(1L)
        assertEquals(expectedNextReview, updatedCard.nextReviewAt)
        assertEquals(now, updatedCard.updatedAt)
    }

    @Test
    fun testCalculateNextReview_whenRemembered_setsMasteredAndCalculatesDaysCorrectly() {
        // 1. Arrange: correctCount = 3
        val now = 1000000000L
        val card = Flashcard(
            id = 2,
            deckId = 101,
            frontText = "Front",
            backText = "Back",
            correctCount = 3,
            wrongCount = 0,
            isMastered = false,
            createdAt = now,
            updatedAt = now
        )

        // 2. Act: Ôn tập thành công với chất lượng REMEMBERED
        val updatedCard = card.calculateNextReview(ReviewQuality.REMEMBERED, now)

        // 3. Assert
        assertTrue(updatedCard.isMastered)
        assertEquals(4, updatedCard.correctCount) // correctCount tăng 1 (từ 3 lên 4)
        assertEquals(card.wrongCount, updatedCard.wrongCount) // Không đổi
        
        // Công thức tính số ngày: (correctCount + 2) ngày = (3 + 2) = 5 ngày
        val expectedNextReview = now + TimeUnit.DAYS.toMillis(5L)
        assertEquals(expectedNextReview, updatedCard.nextReviewAt)
    }

    @Test
    fun testCalculateNextReview_whenEasy_setsMasteredAndCalculatesDaysCorrectly() {
        // 1. Arrange: correctCount = 2
        val now = 1000000000L
        val card = Flashcard(
            id = 3,
            deckId = 101,
            frontText = "Front",
            backText = "Back",
            correctCount = 2,
            wrongCount = 0,
            isMastered = false,
            createdAt = now,
            updatedAt = now
        )

        // 2. Act: Ôn tập với chất lượng EASY
        val updatedCard = card.calculateNextReview(ReviewQuality.EASY, now)

        // 3. Assert
        assertTrue(updatedCard.isMastered)
        assertEquals(3, updatedCard.correctCount) // correctCount tăng 1 (từ 2 lên 3)
        
        // Công thức tính số ngày: (correctCount + 1) * 7 ngày = (2 + 1) * 7 = 21 ngày
        val expectedNextReview = now + TimeUnit.DAYS.toMillis(21L)
        assertEquals(expectedNextReview, updatedCard.nextReviewAt)
    }
}
