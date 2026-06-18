package com.example.wordofday.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Gold500,
    onPrimary = PureWhite,
    primaryContainer = Gold300,
    onPrimaryContainer = SharpInk,
    secondary = Sage500,
    onSecondary = PureWhite,
    secondaryContainer = Sage300,
    onSecondaryContainer = SharpInk,
    background = PureWhite,
    onBackground = SharpInk,
    surface = PureWhite,
    onSurface = SharpInk,
    surfaceContainerHighest = Ink100,
    surfaceContainerHigh = PureWhite,
    surfaceContainer = PureWhite,
    surfaceContainerLow = PureWhite,
    surfaceContainerLowest = PureWhite,
    surfaceVariant = Ink100,
    onSurfaceVariant = Ink600,
    outline = Ink200,
    error = Crimson400,
    onError = PureWhite,
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
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WordOfDayTypography,
        content = content
    )
}
