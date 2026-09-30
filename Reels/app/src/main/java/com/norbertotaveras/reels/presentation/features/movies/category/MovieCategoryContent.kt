/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun MovieCategoryContent(
    state: MovieCategoryUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (MovieCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val refreshState = movies.loadState.refresh) {
        is LoadState.Loading -> {
            MovieCategoryLoadingState(
                modifier = modifier,
                category = state.category,
                onBackClick = {
                    onEvent(MovieCategoryEvent.BackClicked)
                }
            )
            return
        }

        is LoadState.Error -> {
            MovieCategoryErrorState(
                modifier = modifier,
                category = state.category,
                message = refreshState.error.toUserMessage(
                    fallback = "Unable to load ${state.category.label} movies."
                ),
                retryLabel = "Try Again",
                onBackClick = {
                    onEvent(MovieCategoryEvent.BackClicked)
                },
                onRetry = movies::retry,
            )
            return
        }

        is LoadState.NotLoading -> Unit
    }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            start = ReelsDimensions.categoryScreenHorizontalPadding,
            top = ReelsDimensions.categoryScreenTopPadding,
            end = ReelsDimensions.categoryScreenHorizontalPadding,
            bottom = ReelsDimensions.categoryScreenBottomPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridHorizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridVerticalSpacing)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            MovieCategoryHeader(
                category = state.category,
                movieCount = movies.itemCount,
                hasMoreMovies = movies.loadState.append.let { loadState ->
                    loadState !is LoadState.NotLoading || !loadState.endOfPaginationReached
                },
                onBackClick = {
                    onEvent(MovieCategoryEvent.BackClicked)
                }
            )
        }

        if (movies.itemCount == 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    title = "No movies found",
                    message = "Try refreshing this section again in a moment."
                )
            }
        } else {
            items(
                count = movies.itemCount,
                key = { index ->
                    val movie = movies.peek(index)
                    "${movie?.id ?: "placeholder"}-$index"
                }
            ) { index ->
                val movie = movies[index] ?: return@items

                MovieCategoryPosterCard(
                    movie = movie,
                    onClick = {
                        onEvent(MovieCategoryEvent.MovieClicked(movieId = movie.id))
                    }
                )
            }
        }

        when (val appendState = movies.loadState.append) {
            is LoadState.Loading -> {
                items(CATEGORY_APPEND_PLACEHOLDER_COUNT) {
                    MovieCategoryPlaceholderCard()
                }
            }

            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    InlineRetryState(
                        message = appendState.error.toUserMessage(
                            fallback = "Unable to load more movies."
                        ),
                        retryLabel = "Retry",
                        onRetry = movies::retry
                    )
                }
            }

            is LoadState.NotLoading -> {
                if (appendState.endOfPaginationReached && movies.itemCount > 0) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        MovieCategoryEndCap(movieCount = movies.itemCount)
                    }
                }
            }
        }
    }
}
