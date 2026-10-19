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
    val Background = ColorProvider(day = Color(0xFFFDF9F4), night = Color(0xFF1E1D23))
    val Surface = ColorProvider(day = Color(0xFFF2EDE7), night = Color(0xFF29282E))
    val Primary = ColorProvider(day = Color(0xFF1E1B20), night = Color(0xFFE9E3F0))
    val Secondary = ColorProvider(day = Color(0xFF6E6A78), night = Color(0xFF9894A7))
    val Accent = ColorProvider(day = Color(0xFF9C6820), night = Color(0xFFD4A574))
    val OnAccent = ColorProvider(day = Color(0xFF1C1918), night = Color(0xFF1C1918))
    val Divider = ColorProvider(day = Color(0xFFE4DEE6), night = Color(0xFF3A3840))
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
    val BrandMarkSize = 13.dp
    val BrandMarkCornerSize = 3.dp
    val DividerHeight = 1.dp
}
