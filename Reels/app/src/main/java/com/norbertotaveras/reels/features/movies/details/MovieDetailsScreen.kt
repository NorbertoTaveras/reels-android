package com.norbertotaveras.reels.features.movies.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.ui.components.common.CenteredErrorState
import com.norbertotaveras.reels.ui.components.common.CenteredLoadingState
import com.norbertotaveras.reels.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.ui.components.trailer.TrailerCarouselItem
import com.norbertotaveras.reels.ui.extensions.toTmdbImageUrl

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onTrailerClick: (String) -> Unit,
    onSimilarMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
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
                onRetry = onRetry
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
                onBackClick = onBackClick,
                onFavoriteClick = {},
                onCastClick = onCastClick,
                onCastSeeAllClick = {},
                onTrailerClick = { trailerId ->
                    state.trailers
                        .firstOrNull { trailer -> trailer.id == trailerId }
                        ?.youtubeUrl
                        ?.let(onTrailerClick)
                },
                onSimilarMovieClick = onSimilarMovieClick
            )
        }
    }
}
