/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor
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
                .aspectRatio(ReelsRatios.poster)
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
                lineHeight = 16.sp
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
                    lineHeight = 13.sp
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
                text = String.format("%.1f", rating),
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
                .aspectRatio(ReelsRatios.poster)
                .clip(RoundedCornerShape(ReelsDimensions.discoverPosterCornerSize))
                .background(reelsPlaceholderColor())
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .size(height = 14.dp, width = 1.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(reelsPlaceholderColor())
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .size(height = 10.dp, width = 1.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(reelsPlaceholderColor())
        )
    }
}

@Composable
internal fun DiscoverEmptyState(
    hasActiveFilters: Boolean,
    onAdjustFilters: () -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Text(
            text = if (hasActiveFilters) "No matches found" else "No movies found",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = if (hasActiveFilters) {
                "Try adjusting or clearing your filters to see more movies."
            } else {
                "Discover is empty right now. Try again in a moment."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onAdjustFilters,
            shape = MaterialTheme.shapes.large
        ) {
            Text(text = "Adjust filters")
        }

        if (hasActiveFilters) {
            TextButton(onClick = onClearFilters) {
                Text(text = "Clear all filters")
            }
        }
    }
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
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.discoverStateHorizontalPadding,
                vertical = ReelsDimensions.discoverStateVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.discoverStateIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.discoverStateIconCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.discoverStateIconSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(ReelsDimensions.discoverStateIconToTitleSpacing))

        Text(
            text = "Couldn't load movies",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                lineHeight = 28.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(ReelsDimensions.discoverStateTitleToMessageSpacing))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 23.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ReelsDimensions.discoverStateMessageToActionSpacing))

        Button(
            onClick = onRetry,
            shape = MaterialTheme.shapes.large,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.discoverStateActionIconSize)
            )
            Spacer(modifier = Modifier.width(ReelsSpacing.small))
            Text(text = "Retry")
        }
    }
}

@Composable
internal fun DiscoverCachedResultsBanner(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column {
            Row(
                modifier = Modifier.padding(
                    horizontal = ReelsDimensions.discoverOfflineBannerHorizontalPadding,
                    vertical = ReelsDimensions.discoverOfflineBannerVerticalPadding
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    ReelsDimensions.discoverOfflineBannerContentSpacing
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(
                        ReelsDimensions.discoverOfflineBannerIconContainerSize
                    ),
                    shape = RoundedCornerShape(
                        ReelsDimensions.discoverOfflineBannerIconCornerSize
                    ),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.WifiOff,
                            contentDescription = null,
                            modifier = Modifier.size(
                                ReelsDimensions.discoverOfflineBannerIconSize
                            )
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Offline · Cached results",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Reconnect to refresh",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            lineHeight = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(ReelsDimensions.discoverOfflineBannerDismissSize)
                        .clickable(onClick = onDismiss),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss offline notice",
                            modifier = Modifier.size(
                                ReelsDimensions.discoverOfflineBannerDismissIconSize
                            )
                        )
                    }
                }
            }

            HorizontalDivider(color = reelsBorderColor(), thickness = 1.dp)
        }
    }
}
