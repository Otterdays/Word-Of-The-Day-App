package com.example.wordofday.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// [TRACE: DOCS/ROADMAP.md] — §2 Navigation
object AppDestinations {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val QUIZ = "quiz"
    const val LIBRARY = "library"
    const val WORD_DETAIL = "word_detail/{wordKey}"

    fun wordDetailRoute(wordKey: String): String {
        val encoded = URLEncoder.encode(wordKey, StandardCharsets.UTF_8.toString())
        return "word_detail/$encoded"
    }
}
