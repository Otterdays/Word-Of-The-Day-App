package com.example.wordofday.data.model

import kotlinx.serialization.Serializable

// [TRACE: DOCS/ROADMAP.md] — §8a grade levels
@Serializable
enum class GradeLevel(val displayLabel: String, val ageHint: String) {
    PRE_K("Pre-K", "ages 3–4"),
    KINDERGARTEN("Kindergarten", "ages 4–5"),
    GRADE_1("1st Grade", "ages 6–7"),
    GRADE_2("2nd Grade", "ages 7–8"),
    GRADE_3("3rd Grade", "ages 8–9"),
    GRADE_4("4th Grade", "ages 9–10"),
    GRADE_5("5th Grade", "ages 10–11"),
    GRADE_6("6th Grade", "ages 11–12"),
    GRADE_7("7th Grade", "ages 12–13"),
    GRADE_8("8th Grade", "ages 13–14"),
    GRADE_9("9th Grade", "ages 14–15"),
    GRADE_10("10th Grade", "ages 15–16"),
    GRADE_11("11th Grade", "ages 16–17"),
    GRADE_12("12th Grade", "ages 17–18"),
    ADULT("Adult / Enthusiast", "18+"),
}
