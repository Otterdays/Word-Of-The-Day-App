package com.example.wordofday.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — E6 spaced repetition
@Entity(tableName = "word_mastery")
data class WordMasteryEntity(
    @PrimaryKey val wordKey: String,
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val nextReviewDay: Long = 0L,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val masteryLevel: Int = 0,
    val lastReviewedDay: Long = 0L,
)
