package com.example.wordofday.ui.home

import com.example.wordofday.data.model.GradeLevel
import com.example.wordofday.data.model.WordEntry

// [TRACE: DOCS/ROADMAP.md] — §10e grade-adaptive share text
object ShareFormatter {

    fun format(word: WordEntry, grade: GradeLevel): String = when (grade) {
        GradeLevel.PRE_K,
        GradeLevel.KINDERGARTEN,
        GradeLevel.GRADE_1,
        GradeLevel.GRADE_2,
        -> kidFormat(word)

        GradeLevel.GRADE_3,
        GradeLevel.GRADE_4,
        GradeLevel.GRADE_5,
        GradeLevel.GRADE_6,
        GradeLevel.GRADE_7,
        GradeLevel.GRADE_8,
        -> middleFormat(word)

        else -> fullFormat(word)
    }

    private fun kidFormat(word: WordEntry): String = buildString {
        append("Today I learned the word ")
        append(word.word)
        append("! It means ")
        append(word.definition)
        append(". 📚")
    }

    private fun middleFormat(word: WordEntry): String = buildString {
        append(word.word)
        append(" (")
        append(word.partOfSpeech)
        append(") — ")
        append(word.definition)
        if (word.example.isNotBlank()) {
            append("\n\nExample: ")
            append(word.example)
        }
    }

    private fun fullFormat(word: WordEntry): String = buildString {
        append(word.word)
        append(" (")
        append(word.partOfSpeech)
        append(")\n\n")
        append(word.definition)
        if (word.example.isNotBlank()) {
            append("\n\nExample: ")
            append(word.example)
        }
        if (word.etymology.isNotBlank()) {
            append("\n\nEtymology: ")
            append(word.etymology)
        }
    }
}
