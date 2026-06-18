package com.example.wordofday.data.model

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — §10c quiz modes
enum class QuizMode(val displayLabel: String, val description: String) {
    CLASSIC("Classic", "See the word — pick the definition"),
    REVERSE("Reverse", "See the definition — pick the word"),
    BLITZ("Blitz", "Classic mode with a 15-second timer per question"),
}
