/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget.shared

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.glance.color.ColorProvider

internal object PlanWidgetTheme {
    val Background = ColorProvider(day = Color(0xFFFDF9F4), night = Color(0xFF1E1D23))
    val Surface = ColorProvider(day = Color(0xFFF2EDE7), night = Color(0xFF29282E))
    val Primary = ColorProvider(day = Color(0xFF1E1B20), night = Color(0xFFE9E3F0))
    val Secondary = ColorProvider(day = Color(0xFF6E6A78), night = Color(0xFF9894A7))
    // RemoteViews image tints need an opaque color to preserve the intended blended opacity.
    val HeaderAction = ColorProvider(
        day = Color(0x736E6A78).compositeOver(Color(0xFFFDF9F4)),
        night = Color(0x739894A7).compositeOver(Color(0xFF1E1D23)),
    )
    val Accent = ColorProvider(day = Color(0xFF9C6820), night = Color(0xFFD4A574))
    val OnAccent = ColorProvider(day = Color(0xFF1C1918), night = Color(0xFF1C1918))
    val Divider = ColorProvider(day = Color(0xFFE3DDD5), night = Color(0xFF38363F))
    val ImageFallback = ColorProvider(day = Color(0xFF29282E), night = Color(0xFF29282E))
}
