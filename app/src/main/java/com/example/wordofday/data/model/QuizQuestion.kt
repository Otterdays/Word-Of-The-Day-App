package com.example.wordofday.data.model

// [TRACE: DOCS/ROADMAP.md] — §10c quiz mode
data class QuizQuestion(
    val promptWord: WordEntry,
    val options: List<String>,
    val correctIndex: Int,
) {
    init {
        require(options.size == 4) { "Quiz requires exactly 4 options" }
        require(correctIndex in options.indices) { "correctIndex out of range" }
    }
}

data class QuizStats(
    val totalAnswered: Int,
    val totalCorrect: Int,
    val bestStreak: Int,
) {
    val accuracyPercent: Int
        get() = if (totalAnswered == 0) 0 else (totalCorrect * 100) / totalAnswered
}
