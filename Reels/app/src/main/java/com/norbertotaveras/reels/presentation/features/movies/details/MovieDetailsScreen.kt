package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredErrorState
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarouselItem
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onEvent: (MovieDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            MovieDetailsLoadingState(modifier = modifier)
        }

        state.errorMessage != null -> {
            CenteredErrorState(
                modifier = modifier,
                message = state.errorMessage ?: "Unable to load movie details.",
                retryLabel = "Retry",
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
                onBackClick = {
                    onEvent(MovieDetailsEvent.BackClicked)
                },
                onFavoriteClick = {
                    onEvent(MovieDetailsEvent.FavoriteClicked)
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
