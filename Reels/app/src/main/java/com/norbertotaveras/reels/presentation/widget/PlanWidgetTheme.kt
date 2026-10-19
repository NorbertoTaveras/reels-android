/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.color.ColorProvider

internal object PlanWidgetTheme {
    val Background = ColorProvider(day = Color(0xFFF7F2EA), night = Color(0xFF111113))
    val Surface = ColorProvider(day = Color(0xFFEFE8DC), night = Color(0xFF1D1D20))
    val Primary = ColorProvider(day = Color(0xFF201C18), night = Color(0xFFF6F4F1))
    val Secondary = ColorProvider(day = Color(0xFF706357), night = Color(0xFFAEAAB0))
    val Accent = ColorProvider(day = Color(0xFFCF9137), night = Color(0xFFE6A64F))
    val OnAccent = ColorProvider(day = Color(0xFF201C18), night = Color(0xFF201C18))
    val OnImage = ColorProvider(day = Color(0xFFF6F4F1), night = Color(0xFFF6F4F1))
    val OnImageSecondary = ColorProvider(day = Color(0xFFD7D2CD), night = Color(0xFFD7D2CD))
}

internal object PlanWidgetDimensions {
    val CompactSize = DpSize(146.dp, 72.dp)
    val MediumSize = DpSize(220.dp, 146.dp)
    val ExpandedSize = DpSize(294.dp, 146.dp)
    val CornerSize = 16.dp
    val CompactPadding = 8.dp
    val ContentPadding = 12.dp
    val CompactPosterWidth = 28.dp
    val CompactPosterHeight = 40.dp
    val PosterCornerSize = 7.dp
    val MiniPosterWidth = 20.dp
    val MiniPosterHeight = 28.dp
    val MiniPosterCornerSize = 5.dp
    val HeroCornerSize = 10.dp
    val HeroHeight = 64.dp
    val SingleHeroHeight = 110.dp
    val HeroPadding = 8.dp
    val ContentSpacing = 8.dp
}
