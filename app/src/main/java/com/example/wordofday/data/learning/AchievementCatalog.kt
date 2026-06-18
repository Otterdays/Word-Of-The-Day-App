package com.example.wordofday.data.learning

import com.example.wordofday.data.model.Achievement

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — E5/E6 achievement badges
object AchievementCatalog {
    val all: List<Achievement> = listOf(
        Achievement("first_open", "First step", "Open the app for the first time", "🌱"),
        Achievement("streak_7", "Week warrior", "Maintain a 7-day streak", "🔥"),
        Achievement("streak_30", "Monthly master", "Maintain a 30-day streak", "🏆"),
        Achievement("streak_100", "Century scholar", "Maintain a 100-day streak", "👑"),
        Achievement("quiz_10", "Quiz curious", "Answer 10 quiz questions", "❓"),
        Achievement("quiz_100", "Quiz veteran", "Answer 100 quiz questions", "🎯"),
        Achievement("quiz_perfect", "Perfect five", "Score 5/5 in a quiz session", "💯"),
        Achievement("favorite_5", "Collector", "Save 5 favorite words", "⭐"),
        Achievement("favorite_25", "Word hoarder", "Save 25 favorite words", "📚"),
        Achievement("master_5", "Apprentice", "Master 5 words (level 3+)", "🎓"),
        Achievement("master_25", "Lexicon builder", "Master 25 words", "🧠"),
        Achievement("review_10", "Reviewer", "Complete 10 spaced reviews", "🔄"),
        Achievement("explore_50", "Explorer", "Open 50 words from Explore", "🧭"),
        Achievement("hard_5", "Challenge seeker", "Mark 5 words as hard", "💪"),
        Achievement("note_3", "Sentence smith", "Write notes on 3 words", "✍️"),
        Achievement("blitz_win", "Blitz runner", "Finish a Blitz quiz", "⚡"),
        Achievement("reverse_win", "Reverse thinker", "Finish a Reverse quiz", "🔀"),
        Achievement("history_30", "Archivist", "View words on 30 different days", "📅"),
        Achievement("grade_shift", "Flexible learner", "Use easier or harder at least once", "🎚️"),
        Achievement("all_categories", "Renaissance", "Select 5 interest categories", "🌈"),
    )

    fun byId(id: String): Achievement? = all.find { it.id == id }
}
