/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun PlanTypeChoice(
    movieTitle: String,
    isTracked: Boolean,
    showRelease: Boolean,
    onReleaseClick: () -> Unit,
    onMovieNightClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.planSheetHorizontalPadding,
                vertical = ReelsDimensions.planSheetHeaderVerticalPadding,
            ),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.planSheetHeaderSpacing),
        ) {
            Text("Movie plans", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(movieTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            modifier = Modifier.size(ReelsDimensions.planSheetCloseButtonSize),
            shape = RoundedCornerShape(ReelsDimensions.planSheetChoiceIconCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                )
            }
        }
    }
    PlanSheetDivider()
    if (showRelease) {
        PlanChoiceRow(
            icon = {
                Icon(
                    Icons.Filled.EventAvailable,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.planSheetChoiceIconSize),
                )
            },
            title = if (isTracked) "Release reminder" else "Track release",
            subtitle = if (isTracked) "Update reminder settings" else "Get notified when it arrives",
            onClick = onReleaseClick,
        )
    }
    PlanChoiceRow(
        icon = {
            Icon(
                Icons.Filled.Movie,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.planSheetChoiceIconSize),
            )
        },
        title = "Plan a movie night",
        subtitle = "Choose when you want to watch",
        onClick = onMovieNightClick,
    )
}

@Composable
private fun PlanChoiceRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = ReelsDimensions.planSheetHorizontalPadding,
                vertical = ReelsDimensions.planSheetChoiceVerticalPadding,
            ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.planSheetChoiceSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.planSheetChoiceIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.planSheetChoiceIconCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    icon()
                }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(ReelsDimensions.planSheetChoiceChevronSize),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    PlanSheetDivider()
}
