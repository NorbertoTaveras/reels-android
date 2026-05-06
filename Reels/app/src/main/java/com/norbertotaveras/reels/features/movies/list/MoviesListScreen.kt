package com.norbertotaveras.reels.features.movies.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.features.movies.list.components.MovieCategoryChips
import com.norbertotaveras.reels.features.movies.list.components.MovieListItem
import com.norbertotaveras.reels.model.constant.MovieCategory
import com.norbertotaveras.reels.model.data.MovieDataModel
import com.norbertotaveras.reels.ui.components.CenteredErrorState
import com.norbertotaveras.reels.ui.components.CenteredLoadingState

@Composable
fun MoviesListScreen(
    state: MoviesListState,
    movies: LazyPagingItems<MovieDataModel>,
    onCategorySelected: (MovieCategory) -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAppending = movies.loadState.append is LoadState.Loading

    when (val refreshState = movies.loadState.refresh) {
        is LoadState.Loading -> {
            CenteredLoadingState(modifier = modifier)
            return
        }

        is LoadState.Error -> {
            CenteredErrorState(
                modifier = modifier,
                message = refreshState.error.localizedMessage ?: "Unable to load movies right now.",
                retryLabel = "Try Again",
                onRetry = movies::retry
            )
            return
        }

        is LoadState.NotLoading -> Unit
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Cinematics",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text(
                    text = "Browse what to watch next.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                MovieCategoryChips(
                    selectedCategory = state.category,
                    onCategorySelected = onCategorySelected
                )
            }

            items(
                count = movies.itemCount,
                key = { index ->
                    movies.itemSnapshotList.items.getOrNull(index)?.let { movie ->
                        "${movie.id}-$index"
                    } ?: "movie-placeholder-$index"
                }
            ) { index ->
                val movie = movies[index] ?: return@items
                MovieListItem(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }

            when (val appendState = movies.loadState.append) {
                is LoadState.Error -> {
                    item {
                        Text(
                            text = appendState.error.localizedMessage ?: "Unable to load more movies.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    if (movies.itemCount == 0) {
                        item {
                            Text(
                                text = "No movies found for this category.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                is LoadState.Loading -> Unit
            }
        }

        if (isAppending) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
