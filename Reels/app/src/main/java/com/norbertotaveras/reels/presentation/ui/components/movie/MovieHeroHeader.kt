/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

@Composable
fun MovieHeroHeader(
    title: String,
    backdropUrl: String?,
    posterUrl: String?,
    metadata: List<String>,
    rating: String?,
    genres: List<String>,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onRatingClick: () -> Unit,
    onPlanClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavoriteLoading: Boolean = false,
    isWatchlisted: Boolean = false,
    tmdbRating: Double? = null,
    isAccountActionLoading: Boolean = false,
    statusContent: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(
                ReelsDimensions.detailsHeaderHeight +
                    if (statusContent != null) {
                        ReelsDimensions.detailsOfflineNoticeHeight +
                            ReelsDimensions.detailsPosterRowToChipsSpacing
                    } else {
                        0.dp
                    }
            )
    ) {
        Box {
            AsyncImage(
                model = backdropUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ReelsDimensions.detailsHeroHeight),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ReelsDimensions.detailsHeroHeight)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Black.copy(alpha = 0.16f),
                                0.42f to Color.Black.copy(alpha = 0.10f),
                                0.72f to MaterialTheme.colorScheme.background.copy(alpha = 0.28f),
                                1.0f to MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            FilledIconButton(
                modifier = Modifier
                    .padding(
                        start = ReelsSpacing.screenHorizontal,
                        top = ReelsDimensions.detailsHeroTopActionPadding
                    )
                    .size(ReelsDimensions.detailsBackButtonSize),
                onClick = onBackClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ReelsSpacing.screenHorizontal)
                .padding(top = ReelsDimensions.detailsPosterRowTopPadding),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsPosterRowToChipsSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
                verticalAlignment = Alignment.Bottom
            ) {
                Card(
                    modifier = Modifier
                        .width(ReelsDimensions.detailsPosterWidth)
                        .aspectRatio(ReelsRatios.poster),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ReelsRatios.poster)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    MovieMetadataRow(items = metadata)

                    HeroActionRow(
                        rating = rating,
                        isFavorite = isFavorite,
                        isFavoriteLoading = isFavoriteLoading,
                        isWatchlisted = isWatchlisted,
                        tmdbRating = tmdbRating,
                        isAccountActionLoading = isAccountActionLoading,
                        onFavoriteClick = onFavoriteClick,
                        onWatchlistClick = onWatchlistClick,
                        onRatingClick = onRatingClick,
                        onPlanClick = onPlanClick,
                    )
                }
            }

            statusContent?.invoke()

            GenreChipRow(genres = genres)
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun MovieHeroHeaderPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MovieHeroHeader(
                title = "Dune: Part Two",
                backdropUrl = null,
                posterUrl = null,
                metadata = listOf("2024", "166 min", "PG-13"),
                rating = "8.7",
                genres = listOf("Sci-Fi", "Adventure", "Drama"),
                isFavorite = false,
                onBackClick = {},
                onFavoriteClick = {},
                onWatchlistClick = {},
                onRatingClick = {},
                onPlanClick = {},
            )
        }
    }
}
