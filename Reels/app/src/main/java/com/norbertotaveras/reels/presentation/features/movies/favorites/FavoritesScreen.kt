package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.presentation.ui.components.movie.MoviePosterCard
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onEvent: (FavoritesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.movies.isEmpty()) {
        EmptyFavoritesState(modifier = modifier)
        return
    }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = ReelsSpacing.screenHorizontal,
            top = ReelsSpacing.xLarge,
            end = ReelsSpacing.screenHorizontal,
            bottom = ReelsSpacing.xxLarge
        ),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        item(
            span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan)
            }
        ) {
            Text(
                text = "Favorites",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item(
            span = {
                androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan)
            }
        ) {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.DownloadDone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Available offline",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(
            items = state.movies,
            key = { movie -> movie.id }
        ) { movie ->
            MoviePosterCard(
                title = movie.title,
                posterUrl = movie.posterPath.toTmdbImageUrl(),
                releaseYear = movie.releaseDate?.take(4),
                rating = String.format("%.1f", movie.voteAverage),
                onClick = {
                    onEvent(FavoritesEvent.MovieClicked(movieId = movie.id))
                }
            )
        }
    }
}

@Composable
private fun EmptyFavoritesState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(ReelsSpacing.screenHorizontal),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No favorites yet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
