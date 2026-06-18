package com.example.wordofday.data.learning

import com.example.wordofday.data.local.entity.WordMasteryEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Sm2SchedulerTest {

    @Test
    fun `wrong answer resets repetitions and schedules tomorrow`() {
        val current = WordMasteryEntity(
            wordKey = "alpha|5",
            repetitions = 3,
            intervalDays = 7,
            easeFactor = 2.5f,
            wrongCount = 1,
        )
        val next = Sm2Scheduler.scheduleReview(current, "alpha|5", quality = 1, today = 100)
        assertEquals(0, next.repetitions)
        assertEquals(1, next.intervalDays)
        assertEquals(101L, next.nextReviewDay)
        assertEquals(2, next.wrongCount)
    }

    @Test
    fun `correct answer increases interval and mastery`() {
        val current = WordMasteryEntity(
            wordKey = "beta|5",
            repetitions = 2,
            intervalDays = 3,
            easeFactor = 2.5f,
            masteryLevel = 2,
        )
        val next = Sm2Scheduler.scheduleReview(current, "beta|5", quality = 4, today = 50)
        assertEquals(3, next.repetitions)
        assertTrue(next.intervalDays >= 3)
        assertTrue(next.nextReviewDay > 50)
        assertTrue(next.masteryLevel >= 3)
    }

    @Test
    fun `seedForNewWord is due today`() {
        val today = 200L
        val seed = Sm2Scheduler.seedForNewWord("gamma|5", today)
        assertEquals(today, seed.nextReviewDay)
        assertEquals(0, seed.intervalDays)
    }
}
