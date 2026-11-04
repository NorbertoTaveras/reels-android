/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.category

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPosterRatingContainerColor
import java.util.Locale

@Composable
internal fun MovieCategoryPlaceholderCard(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.POSTER)
                .clip(RoundedCornerShape(ReelsDimensions.posterGridPlaceholderCornerSize))
                .background(reelsPlaceholderColor())
        )

        Spacer(modifier = Modifier.height(ReelsDimensions.posterGridPlaceholderPosterToTitleSpacing))
        CategoryPlaceholderBar(
            widthFraction = ReelsDimensions.POSTER_GRID_PLACEHOLDER_TITLE_WIDTH_FRACTION,
            height = ReelsDimensions.posterGridPlaceholderTitleHeight
        )
        Spacer(modifier = Modifier.height(ReelsDimensions.posterGridPlaceholderTitleToYearSpacing))
        CategoryPlaceholderBar(
            widthFraction = ReelsDimensions.POSTER_GRID_PLACEHOLDER_YEAR_WIDTH_FRACTION,
            height = ReelsDimensions.posterGridPlaceholderYearHeight
        )
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
            .clip(RoundedCornerShape(ReelsDimensions.posterGridPlaceholderBarCornerSize))
            .background(reelsPlaceholderColor())
    )
}

@Composable
internal fun MovieCategoryPosterCard(
    movie: MovieDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.POSTER)
                .clip(RoundedCornerShape(ReelsDimensions.categoryPosterCornerSize))
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

        Spacer(modifier = Modifier.height(ReelsDimensions.categoryPosterToTitleSpacing))

        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.5f.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.releaseDate.toReleaseYear()?.let { year ->
            Spacer(modifier = Modifier.height(ReelsDimensions.categoryTitleToYearSpacing))

            Text(
                text = year,
                style = MaterialTheme.typography.labelMedium.copy(
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
private fun CategoryRatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ReelsDimensions.categoryRatingCornerSize),
        color = reelsPosterRatingContainerColor(),
        contentColor = reelsImageRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.categoryRatingHorizontalPadding,
                vertical = ReelsDimensions.categoryRatingVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(
                ReelsDimensions.categoryRatingContentSpacing
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.categoryRatingIconSize)
            )

            Text(
                text = String.format(Locale.getDefault(), "%.1f", rating),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
