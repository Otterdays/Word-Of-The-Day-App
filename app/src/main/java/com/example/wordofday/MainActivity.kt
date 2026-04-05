package com.example.wordofday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wordofday.ui.theme.WordOfDayTheme
import com.example.wordofday.ui.WordOfDayApp

// [TRACE: DOCS/ROADMAP.md] — Roadmap §2 "App shell and UX foundation"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordOfDayTheme {
                WordOfDayApp()
            }
        }
    }
}
