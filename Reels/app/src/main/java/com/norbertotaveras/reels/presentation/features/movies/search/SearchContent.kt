/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.norbertotaveras.reels.presentation.ui.theme.ReelsGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.isReelsLightTheme

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
