/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.CachedContentBanner
import com.norbertotaveras.reels.presentation.ui.components.common.MessageState
import com.norbertotaveras.reels.presentation.ui.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPosterRatingContainerColor

@Composable
internal fun DiscoverPosterCard(
    movie: MovieDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.POSTER)
                .clip(RoundedCornerShape(ReelsDimensions.discoverPosterCornerSize))
        ) {
            AsyncImage(
                model = movie.posterPath.toTmdbImageUrl(),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            if (movie.posterPath.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Outlined.MovieFilter,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DiscoverRatingBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(ReelsDimensions.discoverRatingEdgePadding),
                rating = movie.voteAverage
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.5f.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(ReelsDimensions.discoverPosterTextSpacing))

        movie.releaseDate.toReleaseYear()?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DiscoverRatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ReelsDimensions.discoverRatingCornerSize),
        color = reelsPosterRatingContainerColor(),
        contentColor = reelsImageRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.discoverRatingHorizontalPadding,
                vertical = ReelsDimensions.discoverRatingVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.discoverRatingIconSize)
            )

            Text(
                text = String.format(LocalLocale.current.platformLocale, "%.1f", rating),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
internal fun DiscoverPlaceholderCard(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.POSTER)
                .clip(RoundedCornerShape(ReelsDimensions.posterGridPlaceholderCornerSize))
                .background(reelsPlaceholderColor())
        )

        Spacer(
            modifier = Modifier.height(
                ReelsDimensions.posterGridPlaceholderPosterToTitleSpacing
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(ReelsDimensions.POSTER_GRID_PLACEHOLDER_TITLE_WIDTH_FRACTION)
                .height(ReelsDimensions.posterGridPlaceholderTitleHeight)
                .clip(
                    RoundedCornerShape(ReelsDimensions.posterGridPlaceholderBarCornerSize)
                )
                .background(reelsPlaceholderColor())
        )

        Spacer(
            modifier = Modifier.height(
                ReelsDimensions.posterGridPlaceholderTitleToYearSpacing
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(ReelsDimensions.POSTER_GRID_PLACEHOLDER_YEAR_WIDTH_FRACTION)
                .height(ReelsDimensions.posterGridPlaceholderYearHeight)
                .clip(
                    RoundedCornerShape(ReelsDimensions.posterGridPlaceholderBarCornerSize)
                )
                .background(reelsPlaceholderColor())
        )
    }
}

@Composable
internal fun DiscoverEmptyState(
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessageState(
        modifier = modifier,
        icon = Icons.Filled.Search,
        title = "No results found",
        message = "Try a different title or keyword.",
        actionLabel = "Clear filters",
        onAction = onClearFilters,
        verticalPadding = ReelsDimensions.stateNoResultsVerticalPadding,
        actionHeight = ReelsDimensions.stateCompactActionHeight,
        actionHorizontalPadding = ReelsDimensions.stateCompactActionHorizontalPadding,
        actionVerticalPadding = ReelsDimensions.stateCompactActionVerticalPadding
    )
}

@Composable
internal fun DiscoverEndOfList(
    movieCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
        Text(
            text = "All ${movieCount.toMovieCountLabel()} shown",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun DiscoverErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessageState(
        modifier = modifier,
        icon = Icons.Outlined.ErrorOutline,
        title = "Couldn't load movies",
        message = "Check your connection and try again.",
        actionLabel = "Retry",
        actionIcon = Icons.Outlined.Refresh,
        onAction = onRetry
    )
}

@Composable
internal fun DiscoverCachedResultsBanner(
    isOffline: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CachedContentBanner(
        modifier = modifier,
        message = if (isOffline) "Offline" else "Showing cached results",
        supportingText = if (isOffline) {
            "Saved library available"
        } else {
            "Try again shortly"
        },
        onDismiss = onDismiss
    )
}
