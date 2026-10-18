/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider

internal object PlanWidgetTheme {
    val Background = ColorProvider(day = Color(0xFFF7F2EA), night = Color(0xFF111113))
    val Surface = ColorProvider(day = Color(0xFFEFE8DC), night = Color(0xFF1D1D20))
    val Primary = ColorProvider(day = Color(0xFF201C18), night = Color(0xFFF6F4F1))
    val Secondary = ColorProvider(day = Color(0xFF706357), night = Color(0xFFAEAAB0))
    val Accent = ColorProvider(day = Color(0xFFCF9137), night = Color(0xFFE6A64F))
}
