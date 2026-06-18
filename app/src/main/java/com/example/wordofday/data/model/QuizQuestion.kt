package com.example.wordofday.data.model

// [TRACE: DOCS/ROADMAP.md] — §10c quiz mode
data class QuizQuestion(
    val promptWord: WordEntry,
    val options: List<String>,
    val correctIndex: Int,
    val mode: QuizMode = QuizMode.CLASSIC,
) {
    init {
        require(options.size == 4) { "Quiz requires exactly 4 options" }
        require(correctIndex in options.indices) { "correctIndex out of range" }
    }

    val promptText: String
        get() = when (mode) {
            QuizMode.REVERSE -> promptWord.definition
            else -> promptWord.word
        }

    val promptCaption: String
        get() = when (mode) {
            QuizMode.REVERSE -> "Which word matches this definition?"
            QuizMode.BLITZ -> "Quick! What does this word mean?"
            QuizMode.CLASSIC -> "What does this word mean?"
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
