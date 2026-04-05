package com.example.wordofday.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Gold500,
    onPrimary = Ink50,
    primaryContainer = Gold300,
    onPrimaryContainer = Ink800,
    secondary = Sage500,
    onSecondary = Ink50,
    secondaryContainer = Sage300,
    onSecondaryContainer = Ink800,
    surface = Ink50,
    onSurface = Ink800,
    surfaceContainerHigh = Ink100,
    surfaceContainerLowest = Ink50,
    onSurfaceVariant = Ink500,
    error = Crimson400,
    onError = Ink50
)

private val DarkColorScheme = darkColorScheme(
    primary = Gold400,
    onPrimary = Ink900,
    primaryContainer = Gold600,
    onPrimaryContainer = Ink50,
    secondary = Sage400,
    onSecondary = Ink900,
    secondaryContainer = Sage500,
    onSecondaryContainer = Ink50,
    surface = Ink900,
    onSurface = Ink100,
    surfaceContainerHigh = Ink800,
    surfaceContainerLowest = Ink900,
    onSurfaceVariant = Ink300,
    error = Crimson400,
    onError = Ink50
)

@Composable
fun WordOfDayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Dynamic color on Android 12+ if requested; fall back to curated palette
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WordOfDayTypography,
        content = content
    )
}
