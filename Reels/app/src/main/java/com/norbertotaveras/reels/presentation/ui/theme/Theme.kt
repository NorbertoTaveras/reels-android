/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.norbertotaveras.reels.domain.model.settings.ImageQuality

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
    outline = ReelsBorder,
    outlineVariant = ReelsBorder,
    error = ReelsError
)

private val LightColorScheme = lightColorScheme(
    primary = ReelsLightAccent,
    onPrimary = ReelsLightText,
    secondary = ReelsTeal,
    onSecondary = ReelsLightText,
    background = ReelsLightBackground,
    onBackground = ReelsLightText,
    surface = ReelsLightSurface,
    onSurface = ReelsLightText,
    surfaceVariant = ReelsLightSurfaceHigh,
    onSurfaceVariant = ReelsLightTextSecondary,
    outline = ReelsLightBorder,
    outlineVariant = ReelsLightBorder,
    error = ReelsError
)

@Composable
fun ReelsTheme(
    darkTheme: Boolean = true,
    imageQuality: ImageQuality = ImageQuality.High,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalImageQuality provides imageQuality) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = ReelsShapes,
            content = content
        )
    }
}

@Composable
fun isReelsLightTheme(): Boolean {
    return MaterialTheme.colorScheme.background == ReelsLightBackground
}

@Composable
fun reelsRatingContainerColor(): androidx.compose.ui.graphics.Color {
    return if (isReelsLightTheme()) {
        ReelsLightRatingContainer
    } else {
        ReelsRatingContainer
    }
}

@Composable
fun reelsRatingContentColor(): androidx.compose.ui.graphics.Color {
    return MaterialTheme.colorScheme.primary
}

@Composable
fun reelsPosterRatingContainerColor(): androidx.compose.ui.graphics.Color {
    return ReelsBackground.copy(alpha = 0.80f)
}
