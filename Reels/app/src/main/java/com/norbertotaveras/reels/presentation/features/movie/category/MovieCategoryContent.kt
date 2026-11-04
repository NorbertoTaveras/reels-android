/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.category

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
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.InlineNetworkNotice
import com.norbertotaveras.reels.presentation.ui.components.common.LoadMoreErrorState
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun MovieCategoryContent(
    state: MovieCategoryUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (MovieCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refreshState = movies.loadState.refresh
    val cachedRefreshError = movies.loadState.mediator?.refresh as? LoadState.Error
    val hasCachedMovies = movies.itemCount > 0

    when {
        refreshState is LoadState.Loading && !hasCachedMovies -> {
            MovieCategoryLoadingState(
                modifier = modifier,
                category = state.category,
                onBackClick = {
                    onEvent(MovieCategoryEvent.BackClicked)
                }
            )
            return
        }

        refreshState is LoadState.Error && !hasCachedMovies -> {
            MovieCategoryErrorState(
                modifier = modifier,
                category = state.category,
                retryLabel = "Retry",
                onBackClick = {
                    onEvent(MovieCategoryEvent.BackClicked)
                },
                onRetry = movies::retry,
            )
            return
        }
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

        if (cachedRefreshError != null || refreshState is LoadState.Error) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                InlineNetworkNotice(onRetry = movies::retry)
            }
        }

        if (movies.itemCount == 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                MovieCategoryEmptyState(category = state.category)
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
                    LoadMoreErrorState(onRetry = movies::retry)
                }
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
