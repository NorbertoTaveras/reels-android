/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun LibraryPosterPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            ReelsDimensions.posterGridPlaceholderPosterToTitleSpacing
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(RoundedCornerShape(ReelsDimensions.posterGridPlaceholderCornerSize))
                .background(reelsPlaceholderColor())
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(
                ReelsDimensions.posterGridPlaceholderTitleToYearSpacing
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ReelsDimensions.posterGridPlaceholderTitleWidthFraction)
                    .height(ReelsDimensions.posterGridPlaceholderTitleHeight)
                    .clip(
                        RoundedCornerShape(ReelsDimensions.posterGridPlaceholderBarCornerSize)
                    )
                    .background(reelsPlaceholderColor())
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(ReelsDimensions.posterGridPlaceholderYearWidthFraction)
                    .height(ReelsDimensions.posterGridPlaceholderYearHeight)
                    .clip(
                        RoundedCornerShape(ReelsDimensions.posterGridPlaceholderBarCornerSize)
                    )
                    .background(reelsPlaceholderColor())
            )
        }
    }
}

@Composable
internal fun LibraryPersonPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.castPortrait)
                .clip(RoundedCornerShape(ReelsDimensions.favoritesPosterCornerSize))
                .background(reelsPlaceholderColor())
        )

        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(reelsPlaceholderColor())
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.42f)
                    .height(11.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(reelsPlaceholderColor())
            )
        }
    }
}

@Composable
internal fun LibraryAppendError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Couldn't load more",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            modifier = Modifier.padding(start = ReelsSpacing.small),
            onClick = onRetry,
            contentPadding = PaddingValues(
                horizontal = ReelsSpacing.medium,
                vertical = ReelsSpacing.xSmall
            )
        ) {
            Text(text = "Retry")
        }
    }
}
