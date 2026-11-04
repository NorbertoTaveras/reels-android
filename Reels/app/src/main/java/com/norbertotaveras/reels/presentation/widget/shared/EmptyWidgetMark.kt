/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.presentation.ui.theme.PlanWidgetDimensions

@Composable
internal fun EmptyWidgetMark(compact: Boolean) {
    val size = if (compact) PlanWidgetDimensions.CompactEmptyMarkSize else PlanWidgetDimensions.EmptyMarkSize
    val background = if (compact) R.drawable.widget_empty_mark_compact else R.drawable.widget_empty_mark
    Box(
        modifier = GlanceModifier
            .size(size)
            .background(ImageProvider(background)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "R",
            style = TextStyle(
                color = PlanWidgetTheme.Accent,
                fontSize = if (compact) 11.sp else 14.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}
