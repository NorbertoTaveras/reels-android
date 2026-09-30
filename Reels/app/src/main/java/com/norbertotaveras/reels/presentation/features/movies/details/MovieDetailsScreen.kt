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
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor
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
                trailers = state.toTrailerCarouselItems(),
                similarMovies = state.toSimilarMovieCarouselItems(),
                isFavorite = state.isFavorite,
                isFavoriteLoading = state.isFavoriteLoading,
                isOfflineFavorite = state.isOfflineFavorite,
                savedPersonIds = state.savedPersonIds,
                savingPersonIds = state.savingPersonIds,
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
