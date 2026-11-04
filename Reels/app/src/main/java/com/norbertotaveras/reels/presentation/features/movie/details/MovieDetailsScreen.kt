/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onEvent: (MovieDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
    onPlanClick: () -> Unit = {},
    hasPlans: Boolean = false,
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
                message = state.errorMessage,
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
                trailers = state.toTrailerCarouselItems(),
                similarMovies = state.toSimilarMovieCarouselItems(),
                isFavorite = state.isFavorite,
                isFavoriteLoading = state.isFavoriteLoading,
                isOfflineContent = state.isOfflineContent,
                cachedAt = state.cachedAt,
                savedPersonIds = state.savedPersonIds,
                savingPersonIds = state.savingPersonIds,
                isWatchlisted = state.isWatchlisted,
                tmdbRating = state.tmdbRating,
                isAccountActionLoading = state.isAccountActionLoading,
                hasPlans = hasPlans,
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
                onPlanClick = onPlanClick,
                onOfflineRefreshClick = {
                    onEvent(MovieDetailsEvent.RetryClicked)
                },
                onCastClick = { personId ->
                    onEvent(MovieDetailsEvent.CastClicked(personId = personId))
                },
                onCastSaveClick = { personId ->
                    onEvent(MovieDetailsEvent.CastSaveClicked(personId = personId))
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
