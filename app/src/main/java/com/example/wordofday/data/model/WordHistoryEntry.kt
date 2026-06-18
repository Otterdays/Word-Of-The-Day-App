package com.example.wordofday.data.model

import java.time.LocalDate

// [TRACE: DOCS/ROADMAP.md] — §10a word history
data class WordHistoryEntry(
    val date: LocalDate,
    val wordKey: String,
    val word: String,
    val gradeLevel: GradeLevel,
)
