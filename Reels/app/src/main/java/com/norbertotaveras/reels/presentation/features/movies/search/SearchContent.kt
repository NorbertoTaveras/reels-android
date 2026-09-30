/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.extensions.toRelativeReleaseAge
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.components.movie.MovieResultRow
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun SearchContent(
    state: SearchUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trimmedQuery = state.query.trim()
    val hasSearchableQuery = trimmedQuery.length >= MIN_SEARCH_QUERY_LENGTH
    var dismissedCachedQuery by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = ReelsSpacing.screenHorizontal)
    ) {
        SearchField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ReelsDimensions.searchScreenTopPadding),
            query = state.query,
            onQueryChanged = { query -> onEvent(SearchEvent.QueryChanged(query)) },
            onClearClick = { onEvent(SearchEvent.ClearQueryClicked) },
            onSearch = { onEvent(SearchEvent.SearchSubmitted) }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = ReelsDimensions.searchFieldToContentSpacing),
            contentPadding = PaddingValues(bottom = ReelsDimensions.searchContentBottomPadding),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultSpacing)
        ) {
            if (!hasSearchableQuery) {
                item {
                    if (trimmedQuery.isBlank()) {
                        SearchIdleContent(
                            recentSearches = state.recentSearches,
                            trendingSearches = state.trendingSearches,
                            onEvent = onEvent
                        )
                    } else {
                        SearchPromptState()
                    }
                }
                return@LazyColumn
            }

            val refreshState = movies.loadState.refresh
            val cachedRefreshError = movies.loadState.mediator?.refresh as? LoadState.Error
            val hasCachedMovies = movies.itemCount > 0

            when {
                refreshState is LoadState.Loading && !hasCachedMovies -> {
                    item { SearchResultLoadingState() }
                }

                refreshState is LoadState.Error && !hasCachedMovies -> {
                    item {
                        SearchErrorState(
                            message = refreshState.error.toUserMessage(
                                fallback = "Couldn't connect to the search service. Check your connection and try again."
                            ),
                            onRetry = movies::retry
                        )
                    }
                }

                !hasCachedMovies -> {
                    item {
                        SearchNoResultsState(
                            query = trimmedQuery,
                            suggestions = state.trendingSearches,
                            onEvent = onEvent
                        )
                    }
                }

                else -> {
                    val hasRefreshError = cachedRefreshError != null || refreshState is LoadState.Error
                    if (hasRefreshError && dismissedCachedQuery != trimmedQuery) {
                        item {
                            SearchCachedResultsBanner(
                                onDismiss = { dismissedCachedQuery = trimmedQuery }
                            )
                        }
                    }

                    item {
                        SearchResultsHeader(
                            count = movies.itemCount,
                            query = trimmedQuery
                        )
                    }

                    items(
                        count = movies.itemCount,
                        key = { index ->
                            val movie = movies.peek(index)
                            "${movie?.id ?: "placeholder"}-$index"
                        }
                    ) { index ->
                        movies[index]?.let { movie ->
                            MovieResultRow(
                                title = movie.title,
                                posterUrl = movie.posterPath.toTmdbImageUrl(),
                                releaseYear = movie.releaseDate.toReleaseYear(),
                                secondaryMetadata = movie.releaseDate.toRelativeReleaseAge(),
                                genres = movie.genreIds
                                    .mapNotNull { state.genreNamesById[it] }
                                    .take(3),
                                rating = String.format("%.1f", movie.voteAverage),
                                onClick = {
                                    onEvent(SearchEvent.MovieClicked(movie.id))
                                }
                            )
                        }
                    }

                    when (val appendState = movies.loadState.append) {
                        is LoadState.Loading -> item { SearchAppendLoadingState() }
                        is LoadState.Error -> {
                            item {
                                InlineRetryState(
                                    modifier = Modifier.padding(vertical = ReelsSpacing.medium),
                                    message = appendState.error.toUserMessage(
                                        fallback = "Unable to load more results."
                                    ),
                                    retryLabel = "Retry",
                                    onRetry = movies::retry
                                )
                            }
                        }
                        is LoadState.NotLoading -> Unit
                    }
                }
            }
        }
    }
}
