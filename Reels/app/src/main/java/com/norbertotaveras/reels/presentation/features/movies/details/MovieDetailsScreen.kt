/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarouselItem
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onEvent: (MovieDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            MovieDetailsLoadingState(
                modifier = modifier,
                onBackClick = {
                    onEvent(MovieDetailsEvent.BackClicked)
                }
            )
        }

        state.errorMessage != null -> {
            MovieDetailsErrorState(
                modifier = modifier,
                message = state.errorMessage ?: "Unable to load movie details.",
                retryLabel = "Retry",
                onBackClick = {
                    onEvent(MovieDetailsEvent.BackClicked)
                },
                onRetry = {
                    onEvent(MovieDetailsEvent.RetryClicked)
                }
            )
        }

        state.movie != null -> {
            val movie = checkNotNull(state.movie)
            MovieDetailsContent(
                modifier = modifier,
                movie = movie,
                cast = state.cast,
                trailers = state.trailers.map { trailer ->
                    TrailerCarouselItem(
                        id = trailer.id,
                        title = trailer.name,
                        thumbnailUrl = trailer.youtubeThumbnailUrl,
                        source = trailer.site,
                        duration = null
                    )
                },
                similarMovies = state.similarMovies.map { movie ->
                    PosterCarouselItem(
                        id = movie.id,
                        title = movie.title,
                        posterUrl = movie.posterPath.toTmdbImageUrl(),
                        releaseYear = movie.releaseDate?.take(4)
                    )
                },
                isFavorite = state.isFavorite,
                isFavoriteLoading = state.isFavoriteLoading,
                isOfflineFavorite = state.isOfflineFavorite,
                isTmdbAccountConnected = state.isTmdbAccountConnected,
                isWatchlisted = state.isWatchlisted,
                tmdbRating = state.tmdbRating,
                isAccountActionLoading = state.isAccountActionLoading,
                onBackClick = {
                    onEvent(MovieDetailsEvent.BackClicked)
                },
                onFavoriteClick = {
                    onEvent(MovieDetailsEvent.FavoriteClicked)
                },
                onWatchlistClick = {
                    onEvent(MovieDetailsEvent.WatchlistClicked)
                },
                onRatingClick = {
                    onEvent(MovieDetailsEvent.RatingClicked)
                },
                onDeleteRatingClick = {
                    onEvent(MovieDetailsEvent.DeleteRatingClicked)
                },
                onCastClick = { personId ->
                    onEvent(MovieDetailsEvent.CastClicked(personId = personId))
                },
                onCastSeeAllClick = {
                    onEvent(MovieDetailsEvent.CastSeeAllClicked)
                },
                onTrailerClick = { trailerId ->
                    onEvent(MovieDetailsEvent.TrailerClicked(trailerId = trailerId))
                },
                onSimilarMovieClick = { movieId ->
                    onEvent(MovieDetailsEvent.SimilarMovieClicked(movieId = movieId))
                }
            )
        }
    }
}

@Composable
private fun MovieDetailsLoadingState(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = ReelsSpacing.xxLarge),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsSectionSpacing)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ReelsDimensions.detailsLoadingHeroHeight)
                    .background(detailsPlaceholderColor())
            ) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = ReelsSpacing.screenHorizontal,
                            top = ReelsDimensions.detailsHeroTopActionPadding
                        )
                        .size(ReelsDimensions.detailsBackButtonSize),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }

                PlaceholderBox(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = ReelsSpacing.screenHorizontal, bottom = ReelsSpacing.large)
                        .width(ReelsDimensions.detailsPosterWidth)
                        .aspectRatio(ReelsRatios.poster)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = ReelsSpacing.screenHorizontal +
                                ReelsDimensions.detailsPosterWidth +
                                ReelsSpacing.medium,
                            end = ReelsSpacing.screenHorizontal,
                            bottom = ReelsSpacing.large
                        ),
                    verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
                ) {
                    PlaceholderBar(width = 180.dp, height = 22.dp)
                    PlaceholderBar(width = 156.dp, height = 16.dp)
                    Row(horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
                        PlaceholderBar(width = 72.dp, height = 28.dp)
                        PlaceholderBar(width = 52.dp, height = 28.dp)
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                PlaceholderBar(width = 118.dp, height = 24.dp)
                PlaceholderBar(width = 280.dp, height = 18.dp)
                PlaceholderBar(width = 310.dp, height = 18.dp)
                PlaceholderBar(width = 230.dp, height = 18.dp)
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                repeat(3) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlaceholderBox(
                            modifier = Modifier.size(ReelsDimensions.detailsInfoIconContainerSize)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                            PlaceholderBar(width = 96.dp, height = 14.dp)
                            PlaceholderBar(width = 132.dp, height = 16.dp)
                        }
                    }
                }
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                items(5) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
                    ) {
                        PlaceholderBox(modifier = Modifier.size(70.dp))
                        PlaceholderBar(width = 68.dp, height = 12.dp)
                    }
                }
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                items(2) {
                    PlaceholderBox(
                        modifier = Modifier
                            .width(ReelsDimensions.detailsTrailerWidth)
                            .height(108.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsErrorState(
    message: String,
    retryLabel: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = ReelsSpacing.screenHorizontal,
                top = ReelsSpacing.large,
                end = ReelsSpacing.screenHorizontal,
                bottom = ReelsSpacing.xxLarge
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsSectionSpacing)
    ) {
        MovieDetailsStateHeader(
            title = "Movie Details",
            onBackClick = onBackClick
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
        ) {
            Column(
                modifier = Modifier.padding(ReelsDimensions.stateCardPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.stateCardContentSpacing)
            ) {
                Surface(
                    modifier = Modifier.size(ReelsDimensions.stateIconContainerSize),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(ReelsDimensions.stateIconSize)
                        )
                    }
                }

                Text(
                    text = message.toErrorTitle(fallback = "Unable to load movie"),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                FilledTonalButton(
                    modifier = Modifier.padding(top = ReelsSpacing.small),
                    onClick = onRetry
                ) {
                    Text(text = retryLabel)
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsStateHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.categoryBackButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(ReelsDimensions.categoryBackIconSize)
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 30.sp,
                lineHeight = 36.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PlaceholderBox(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(detailsPlaceholderColor())
    )
}

@Composable
private fun PlaceholderBar(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(MaterialTheme.shapes.medium)
            .background(detailsPlaceholderColor())
    )
}

@Composable
private fun detailsPlaceholderColor(): Color {
    return MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f)
}
