/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.components.common.MessageState
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun FilmographyPlaceholderCard(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(RoundedCornerShape(ReelsDimensions.filmographyPosterCornerRadius))
                .background(reelsPlaceholderColor())
        )

        Box(
            modifier = Modifier
                .padding(top = ReelsDimensions.filmographyPosterToTitleSpacing)
                .fillMaxWidth(0.82f)
                .height(12.dp)
                .clip(MaterialTheme.shapes.small)
                .background(reelsPlaceholderColor())
        )

        Box(
            modifier = Modifier
                .padding(top = ReelsDimensions.filmographyTitleToYearSpacing)
                .width(36.dp)
                .height(10.dp)
                .clip(MaterialTheme.shapes.small)
                .background(reelsPlaceholderColor())
        )
    }
}

@Composable
internal fun FilmographyEndCap(
    creditCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        Box(
            modifier = Modifier
                .size(width = 32.dp, height = 1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f))
        )

        Text(
            text = "All $creditCount credits",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun FilmographyErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessageState(
        modifier = modifier,
        icon = Icons.Outlined.ErrorOutline,
        title = "Couldn't load credits",
        message = "Check your connection and try again.",
        actionLabel = "Retry",
        actionIcon = Icons.Filled.Refresh,
        onAction = onRetry
    )
}

@Composable
internal fun FilmographyEmptyState(
    personName: String,
    modifier: Modifier = Modifier,
) {
    MessageState(
        modifier = modifier,
        icon = Icons.Outlined.Movie,
        title = "No credits found",
        message = "$personName doesn't have any movie credits yet."
    )
}
