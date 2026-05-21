package com.norbertotaveras.reels.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ReelsAccent,
    onPrimary = ReelsBackground,
    primaryContainer = ReelsAccentMuted,
    onPrimaryContainer = ReelsTextPrimary,
    secondary = ReelsTeal,
    onSecondary = ReelsCharcoal,
    background = ReelsBackground,
    onBackground = ReelsTextPrimary,
    surface = ReelsSurface,
    onSurface = ReelsTextPrimary,
    surfaceVariant = ReelsSurfaceHigh,
    onSurfaceVariant = ReelsTextSecondary,
    error = ReelsError
)

private val LightColorScheme = lightColorScheme(
    primary = ReelsCopperDark,
    onPrimary = ReelsBone,
    secondary = ReelsTeal,
    onSecondary = ReelsLightText,
    background = ReelsLightBackground,
    onBackground = ReelsLightText,
    surface = ReelsLightSurface,
    onSurface = ReelsLightText,
    surfaceVariant = ReelsBone,
    onSurfaceVariant = ReelsCopperDark,
    error = ReelsCopperDark
)

@Composable
fun ReelsTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ReelsShapes,
        content = content
    )
}
