/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
import com.norbertotaveras.reels.presentation.ui.theme.reelsPosterRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun MovieCategoryPlaceholderCard(
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryPosterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.medium)
                .background(reelsPlaceholderColor())
        )

        CategoryPlaceholderBar(widthFraction = 0.92f, height = 12.dp)
        CategoryPlaceholderBar(widthFraction = 0.45f, height = 10.dp)
    }
}

@Composable
private fun CategoryPlaceholderBar(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(MaterialTheme.shapes.medium)
            .background(reelsPlaceholderColor())
    )
}

@Composable
internal fun MovieCategoryPosterCard(
    movie: MovieDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryPosterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = movie.posterPath.toTmdbImageUrl(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            CategoryRatingBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(ReelsDimensions.categoryRatingEdgePadding),
                rating = movie.voteAverage
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.releaseDate.toReleaseYear()?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CategoryRatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = reelsPosterRatingContainerColor(),
        contentColor = reelsImageRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.categoryRatingHorizontalPadding,
                vertical = ReelsDimensions.categoryRatingVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.categoryRatingIconSize)
            )

            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun MovieCategoryEndCap(
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
        Box(
            modifier = Modifier
                .size(width = 32.dp, height = 1.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
        )

        Text(
            text = "All $movieCount movies loaded",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
