package com.norbertotaveras.reels.features.movies.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.ui.components.CenteredErrorState
import com.norbertotaveras.reels.ui.components.CenteredLoadingState
import com.norbertotaveras.reels.ui.components.TrailerCarouselItem
import com.norbertotaveras.reels.ui.extensions.toTmdbImageUrl

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onTrailerClick: (String) -> Unit,
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
                onSimilarMovieClick = {}
            )
        }
    }
}
