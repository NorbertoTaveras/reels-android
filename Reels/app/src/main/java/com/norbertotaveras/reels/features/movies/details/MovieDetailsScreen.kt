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
import com.norbertotaveras.reels.ui.extensions.toTmdbImageUrl

@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onBackClick: () -> Unit,
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
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Button(onClick = onBackClick) {
                        Text(text = "Back")
                    }
                }

                item {
                    AsyncImage(
                        model = (movie.backdropPath ?: movie.posterPath).toTmdbImageUrl(),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = buildString {
                                append(movie.releaseDate ?: "Unknown release")
                                append(" • ")
                                append(String.format("%.1f", movie.voteAverage))
                                movie.runtime?.let {
                                    append(" • ")
                                    append("$it min")
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (movie.genres.isNotEmpty()) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            movie.genres.take(3).forEach { genre ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(text = genre.name) }
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = movie.overview.ifBlank { "No overview available yet." },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
