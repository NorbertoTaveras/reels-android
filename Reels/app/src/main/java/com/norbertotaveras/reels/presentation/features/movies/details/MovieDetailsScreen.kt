package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredErrorState
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredLoadingState
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarouselItem
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onEvent: (MovieDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            CenteredLoadingState(modifier = modifier)
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
