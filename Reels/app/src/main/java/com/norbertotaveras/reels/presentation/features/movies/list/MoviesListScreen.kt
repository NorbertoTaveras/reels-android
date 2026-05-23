package com.norbertotaveras.reels.presentation.features.movies.list

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
import com.norbertotaveras.reels.presentation.features.movies.list.components.MovieCategoryChips
import com.norbertotaveras.reels.presentation.features.movies.list.components.MovieListItem
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredErrorState
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredLoadingState
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState

@Composable
fun MoviesListScreen(
    state: MoviesListState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (MoviesListEvent) -> Unit,
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
                    onCategorySelected = { category ->
                        onEvent(MoviesListEvent.CategorySelected(category = category))
                    }
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
                    onClick = {
                        onEvent(MoviesListEvent.MovieClicked(movieId = movie.id))
                    }
                )
            }

            when (val appendState = movies.loadState.append) {
                is LoadState.Error -> {
                    item {
                        InlineRetryState(
                            message = appendState.error.localizedMessage
                                ?: "Unable to load more movies.",
                            retryLabel = "Retry",
                            onRetry = movies::retry
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    if (movies.itemCount == 0) {
                        item {
                            EmptyState(
                                title = "No movies found",
                                message = "Try another category or refresh again in a moment."
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
