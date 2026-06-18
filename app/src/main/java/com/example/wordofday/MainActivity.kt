package com.example.wordofday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.example.wordofday.data.preferences.ThemePreferencesRepository
import com.example.wordofday.ui.theme.WordOfDayTheme
import com.example.wordofday.ui.WordOfDayApp
import kotlinx.coroutines.launch

// [TRACE: DOCS/ROADMAP.md] — Roadmap §2 "App shell and UX foundation"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val themeRepository = ThemePreferencesRepository(this)
        setContent {
            val isDarkTheme by themeRepository.isDarkTheme.collectAsState(initial = false)
            val scope = rememberCoroutineScope()
            WordOfDayTheme(darkTheme = isDarkTheme) {
                WordOfDayApp(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { scope.launch { themeRepository.toggleDarkTheme() } },
                )
            }
        }
    }
}
