package com.example.wordofday.data.model

import kotlinx.serialization.Serializable

/**
 * Core domain model for a single word entry.
 * `gradeLevel` / `categories` default for legacy JSON rows without those fields.
 */
@Serializable
data class WordEntry(
    val word: String,
    val partOfSpeech: String,
    val pronunciation: String = "",
    val definition: String,
    val example: String = "",
    val etymology: String = "",
    val gradeLevel: GradeLevel = GradeLevel.ADULT,
    val categories: List<Category> = listOf(Category.GENERAL),
)
