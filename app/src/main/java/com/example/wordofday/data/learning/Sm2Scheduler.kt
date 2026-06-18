package com.example.wordofday.data.learning

import com.example.wordofday.data.local.entity.WordMasteryEntity
import java.time.LocalDate

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — E6 SM-2 lite scheduler
object Sm2Scheduler {

    fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

    fun qualityFromCorrect(correct: Boolean): Int = if (correct) 4 else 1

    fun scheduleReview(
        current: WordMasteryEntity?,
        wordKey: String,
        quality: Int,
        today: Long = todayEpochDay(),
    ): WordMasteryEntity {
        val base = current ?: WordMasteryEntity(wordKey = wordKey)
        if (quality < 3) {
            return base.copy(
                repetitions = 0,
                intervalDays = 1,
                nextReviewDay = today + 1,
                wrongCount = base.wrongCount + 1,
                lastReviewedDay = today,
                masteryLevel = base.masteryLevel.coerceAtMost(2),
            )
        }
        val reps = base.repetitions + 1
        val interval = when (reps) {
            1 -> 1
            2 -> 3
            else -> (base.intervalDays * base.easeFactor).toInt().coerceAtLeast(1)
        }
        val ease = (base.easeFactor + 0.1f - (5 - quality) * 0.08f).coerceIn(1.3f, 3.0f)
        val level = when {
            reps >= 5 -> 5
            reps >= 4 -> 4
            reps >= 3 -> 3
            reps >= 2 -> 2
            else -> 1
        }
        return base.copy(
            repetitions = reps,
            intervalDays = interval,
            easeFactor = ease,
            nextReviewDay = today + interval,
            correctCount = base.correctCount + 1,
            lastReviewedDay = today,
            masteryLevel = maxOf(base.masteryLevel, level),
        )
    }

    fun seedForNewWord(wordKey: String, today: Long = todayEpochDay()): WordMasteryEntity =
        WordMasteryEntity(
            wordKey = wordKey,
            nextReviewDay = today,
            intervalDays = 0,
        )
}
