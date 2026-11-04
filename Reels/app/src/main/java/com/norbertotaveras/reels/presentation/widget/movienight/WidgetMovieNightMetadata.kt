/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget.movienight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.norbertotaveras.reels.presentation.ui.theme.PlanWidgetDimensions
import com.norbertotaveras.reels.presentation.widget.shared.PlanWidgetTheme
import com.norbertotaveras.reels.presentation.widget.shared.WidgetIcon

@Composable
internal fun WidgetMovieNightMetadata(
    date: String,
    runtime: String,
    reminder: String? = null,
    itemSpacing: Dp = PlanWidgetDimensions.MetadataItemSpacing,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        WidgetIcon(Icons.Outlined.AccessTime, PlanWidgetDimensions.NightMetadataIconSize, PlanWidgetTheme.Secondary)
        Spacer(GlanceModifier.width(PlanWidgetDimensions.MetadataIconSpacing))
        Text(
            text = date,
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 9.sp),
        )
        Spacer(GlanceModifier.width(itemSpacing))
        Text(
            text = runtime,
            modifier = if (reminder == null) GlanceModifier.defaultWeight() else GlanceModifier,
            maxLines = 1,
            style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 9.sp),
        )
        if (reminder != null) {
            Spacer(GlanceModifier.width(itemSpacing))
            WidgetIcon(Icons.Outlined.NotificationsNone, PlanWidgetDimensions.NightMetadataIconSize, PlanWidgetTheme.Secondary)
            Spacer(GlanceModifier.width(PlanWidgetDimensions.MetadataIconSpacing))
            Text(
                text = reminder,
                modifier = GlanceModifier.defaultWeight(),
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 9.sp),
            )
        }
    }
}
