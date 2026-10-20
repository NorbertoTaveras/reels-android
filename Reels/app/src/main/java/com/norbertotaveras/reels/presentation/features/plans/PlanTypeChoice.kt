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
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal

@Composable
internal fun PlanTypeChoice(
    movieTitle: String,
    releaseDate: String?,
    isTracked: Boolean,
    movieNightPlan: MovieNightPlan?,
    showRelease: Boolean,
    onReleaseClick: () -> Unit,
    onMovieNightClick: () -> Unit,
    onDismiss: () -> Unit,
    onStopTracking: () -> Unit,
    isSaving: Boolean,
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
            Text(
                "Movie plans",
                style = MaterialTheme.typography.titleMedium.copy(
                    lineHeight = ReelsDimensions.planSheetChoiceTitleLineHeight,
                ),
                fontWeight = FontWeight.Medium,
            )
            Text(movieTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            modifier = Modifier
                .padding(top = ReelsDimensions.planSheetHeaderSpacing)
                .size(ReelsDimensions.planSheetCloseButtonSize),
            shape = RoundedCornerShape(ReelsDimensions.planSheetChoiceCloseCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
    PlanSheetDivider()
    Column(modifier = Modifier.padding(vertical = ReelsDimensions.planSheetChoiceOptionsVerticalPadding)) {
        if (showRelease) {
            PlanChoiceRow(
                icon = {
                    Icon(
                        if (isTracked) Icons.Outlined.EventAvailable else Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.planSheetChoiceIconSize),
                    )
                },
                title = if (isTracked) "Release tracked" else "Track release",
                subtitle = if (isTracked) "Tap to change reminder" else releaseDate.releaseChoiceLabel(),
                onClick = onReleaseClick,
                highlightColor = if (isTracked) MaterialTheme.colorScheme.primary else null,
                highlightContainerAlpha = 0.20f,
            )
            if (isTracked) {
                Text(
                    "Stop tracking",
                    modifier = Modifier
                        .padding(
                            start = ReelsDimensions.planSheetStopTrackingStartPadding,
                            bottom = ReelsDimensions.planSheetChoiceOptionsVerticalPadding,
                        )
                        .clickable(enabled = !isSaving, onClick = onStopTracking),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
        PlanChoiceRow(
            icon = {
                Icon(
                    Icons.Outlined.Theaters,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.planSheetChoiceIconSize),
                )
            },
            title = if (movieNightPlan == null) "Plan a movie night" else "Movie night scheduled",
            subtitle = movieNightPlan?.let { "${it.dateLabel()} · ${it.timeLabel()}" }
                ?: "Choose when you want to watch",
            onClick = onMovieNightClick,
            highlightColor = if (movieNightPlan != null) ReelsTeal else null,
        )
    }
}

@Composable
private fun PlanChoiceRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    highlightColor: Color? = null,
    highlightContainerAlpha: Float = 0.15f,
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
            color = highlightColor?.copy(alpha = highlightContainerAlpha)
                ?: MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                CompositionLocalProvider(
                    LocalContentColor provides (highlightColor ?: MaterialTheme.colorScheme.onSurfaceVariant),
                ) {
                    icon()
                }
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.planSheetHeaderSpacing),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = highlightColor ?: MaterialTheme.colorScheme.onSurface,
            )
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(ReelsDimensions.planSheetChoiceChevronSize),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
