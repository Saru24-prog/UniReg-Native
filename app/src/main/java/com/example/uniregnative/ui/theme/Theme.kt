package com.example.uniregnative.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = CampusNavy,
    onPrimary = White,
    primaryContainer = CampusNavyLight,
    onPrimaryContainer = White,
    secondary = AccentGold,
    onSecondary = Neutral900,
    background = BackgroundLight,
    onBackground = Neutral900,
    surface = SurfaceLight,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral700,
    outline = Neutral300,
    error = ClashRed,
    onError = White,
    errorContainer = ClashRedBg,
    onErrorContainer = ClashRed,
)

private val DarkColors = darkColorScheme(
    primary = CampusNavyLight,
    onPrimary = Neutral900,
    primaryContainer = CampusNavyDark,
    onPrimaryContainer = White,
    secondary = AccentGoldLight,
    onSecondary = Neutral900,
    background = BackgroundDark,
    onBackground = White,
    surface = SurfaceDark,
    onSurface = White,
    surfaceVariant = Color(0xFF2A3644),
    onSurfaceVariant = Neutral300,
    outline = Neutral700,
    error = ClashRed,
    onError = White,
    errorContainer = Color(0xFF4A2020),
    onErrorContainer = Color(0xFFFFB4AB),
)

@Composable
fun UniRegNativeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = UniRegTypography,
        content = content
    )
}