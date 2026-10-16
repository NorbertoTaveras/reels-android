/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun reelsPlaceholderColor(alpha: Float = 1f): Color {
    val transition = rememberInfiniteTransition(label = "reels-placeholder")
    val pulseAlpha by transition.animateFloat(
        initialValue = alpha,
        targetValue = alpha * 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = PLACEHOLDER_PULSE_DURATION_MILLIS,
                easing = PLACEHOLDER_PULSE_EASING
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reels-placeholder-alpha"
    )

    return MaterialTheme.colorScheme.surfaceVariant.copy(alpha = pulseAlpha)
}

private const val PLACEHOLDER_PULSE_DURATION_MILLIS = 1_000
private val PLACEHOLDER_PULSE_EASING = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
