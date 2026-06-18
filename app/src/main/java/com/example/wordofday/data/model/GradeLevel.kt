package com.example.wordofday.data.model

import kotlinx.serialization.Serializable

// [TRACE: DOCS/ROADMAP.md] — §8a grade levels; §8c `assets/words/<base>.json`
@Serializable
enum class GradeLevel(
    val displayLabel: String,
    val ageHint: String,
    /** File base name (no extension) under `assets/words/`. */
    val wordsAssetBaseName: String,
) {
    PRE_K("Pre-K", "ages 3–4", "pre_k"),
    KINDERGARTEN("Kindergarten", "ages 4–5", "kindergarten"),
    GRADE_1("1st Grade", "ages 6–7", "grade_1"),
    GRADE_2("2nd Grade", "ages 7–8", "grade_2"),
    GRADE_3("3rd Grade", "ages 8–9", "grade_3"),
    GRADE_4("4th Grade", "ages 9–10", "grade_4"),
    GRADE_5("5th Grade", "ages 10–11", "grade_5"),
    GRADE_6("6th Grade", "ages 11–12", "grade_6"),
    GRADE_7("7th Grade", "ages 12–13", "grade_7"),
    GRADE_8("8th Grade", "ages 13–14", "grade_8"),
    GRADE_9("9th Grade", "ages 14–15", "grade_9"),
    GRADE_10("10th Grade", "ages 15–16", "grade_10"),
    GRADE_11("11th Grade", "ages 16–17", "grade_11"),
    GRADE_12("12th Grade", "ages 17–18", "grade_12"),
    ADULT("Adult / Enthusiast", "18+", "adult");

    /** Path relative to `assets/` for this grade’s word list. */
    val bundledWordsAssetPath: String get() = "words/$wordsAssetBaseName.json"

    /** Shift by [offset] steps within enum bounds (used for session-only easier/harder). */
    fun withOffset(offset: Int): GradeLevel {
        val target = (ordinal + offset).coerceIn(0, entries.lastIndex)
        return entries[target]
    }

    fun canShiftEasier(): Boolean = ordinal > 0

    fun canShiftHarder(): Boolean = ordinal < entries.lastIndex
}
