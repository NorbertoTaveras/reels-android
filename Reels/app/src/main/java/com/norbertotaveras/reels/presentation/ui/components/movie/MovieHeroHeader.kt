/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.movie

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContentColor

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
    modifier: Modifier = Modifier,
    isFavoriteLoading: Boolean = false,
    isWatchlisted: Boolean = false,
    tmdbRating: Double? = null,
    isAccountActionLoading: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ReelsDimensions.detailsHeaderHeight)
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
                    shape = MaterialTheme.shapes.large,
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
                            .clip(MaterialTheme.shapes.large),
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
                            lineHeight = 25.sp,
                            fontWeight = FontWeight.SemiBold
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
                        onRatingClick = onRatingClick
                    )
                }
            }

            GenreChipRow(genres = genres)
        }
    }
}

@Composable
private fun RatingBadge(
    rating: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = reelsRatingContainerColor(),
        contentColor = reelsRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsSpacing.small,
                vertical = ReelsSpacing.xSmall
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = rating,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun HeroActionRow(
    rating: String?,
    isFavorite: Boolean,
    isFavoriteLoading: Boolean,
    isWatchlisted: Boolean,
    tmdbRating: Double?,
    isAccountActionLoading: Boolean,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onRatingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        if (!rating.isNullOrBlank()) {
            HeroRatingButton(rating = rating)
        }

        HeroFavoriteButton(
            isFavorite = isFavorite,
            isLoading = isFavoriteLoading,
            onClick = onFavoriteClick
        )

        HeroToggleActionButton(
            selected = isWatchlisted,
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder,
            selectedContentDescription = "Remove from TMDB watchlist",
            unselectedContentDescription = "Add to TMDB watchlist",
            selectedColor = ReelsTeal,
            isLoading = isAccountActionLoading,
            onClick = onWatchlistClick
        )

        HeroRateButton(
            rating = tmdbRating,
            isLoading = isAccountActionLoading,
            onClick = onRatingClick
        )
    }
}

@Composable
private fun HeroRatingButton(
    rating: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(ReelsDimensions.detailsActionButtonHeight),
        shape = MaterialTheme.shapes.large,
        color = reelsRatingContainerColor(),
        contentColor = reelsRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = ReelsSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.detailsHeroRatingIconSize)
            )

            Text(
                text = rating,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun HeroFavoriteButton(
    isFavorite: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.detailsActionButtonHeight)
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClickLabel = if (isFavorite) {
                    "Remove from favorites"
                } else {
                    "Add to favorites"
                },
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        contentColor = if (isFavorite) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroToggleActionButton(
    selected: Boolean,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedContentDescription: String,
    unselectedContentDescription: String,
    selectedColor: Color,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.detailsActionButtonHeight)
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClickLabel = if (selected) {
                    selectedContentDescription
                } else {
                    unselectedContentDescription
                },
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        contentColor = if (selected) {
            selectedColor
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroRateButton(
    rating: Double?,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.detailsActionButtonHeight)
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClickLabel = if (rating == null) "Rate on TMDB" else "Update TMDB rating",
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.large,
        color = if (rating == null) {
            MaterialTheme.colorScheme.surface
        } else {
            reelsRatingContainerColor()
        },
        contentColor = if (rating == null) {
            MaterialTheme.colorScheme.onSurface
        } else {
            reelsRatingContentColor()
        },
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = if (rating == null) {
                        Icons.Outlined.StarBorder
                    } else {
                        Icons.Filled.Star
                    },
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
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
                onRatingClick = {}
            )
        }
    }
}
