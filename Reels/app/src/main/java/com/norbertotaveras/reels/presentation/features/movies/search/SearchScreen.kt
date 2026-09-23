/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.extensions.toRelativeReleaseAge
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.components.movie.MovieResultRow
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.isReelsLightTheme

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trimmedQuery = state.query.trim()
    val hasSearchableQuery = trimmedQuery.length >= MIN_SEARCH_QUERY_LENGTH

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = ReelsSpacing.screenHorizontal)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ReelsDimensions.searchScreenTopPadding),
            value = state.query,
            onValueChange = { query ->
                onEvent(SearchEvent.QueryChanged(query = query))
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            placeholder = {
                Text(
                    text = "Search movies",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (state.query.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onEvent(SearchEvent.ClearQueryClicked)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onEvent(SearchEvent.SearchSubmitted)
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.36f),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = ReelsDimensions.searchFieldToContentSpacing),
            contentPadding = PaddingValues(bottom = ReelsDimensions.searchContentBottomPadding),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultSpacing)
        ) {
            if (state.recentSearches.isNotEmpty()) {
                item {
                    RecentSearchesSection(
                        recentSearches = state.recentSearches,
                        onEvent = onEvent
                    )
                }
            }

            if (!hasSearchableQuery) {
                item {
                    SearchPromptState(query = trimmedQuery)
                }
            } else {
                item {
                    SearchResultsHeader()
                }

                when (val refreshState = movies.loadState.refresh) {
                    is LoadState.Loading -> {
                        item {
                            SearchResultLoadingState()
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            InlineRetryState(
                                modifier = Modifier.padding(top = ReelsSpacing.medium),
                                message = refreshState.error.toUserMessage(
                                    fallback = "Unable to search for \"$trimmedQuery\"."
                                ),
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
                                    message = "No results for \"$trimmedQuery\". Try a different title or check the spelling."
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
                                val movie = movies[index]

                                if (movie != null) {
                                    MovieResultRow(
                                        title = movie.title,
                                        posterUrl = movie.posterPath.toTmdbImageUrl(),
                                        releaseYear = movie.releaseDate.toReleaseYear(),
                                        secondaryMetadata = movie.releaseDate.toRelativeReleaseAge(),
                                        genres = movie.genreIds
                                            .mapNotNull { genreId -> state.genreNamesById[genreId] }
                                            .take(3),
                                        rating = String.format("%.1f", movie.voteAverage),
                                        onClick = {
                                            onEvent(SearchEvent.MovieClicked(movieId = movie.id))
                                        }
                                    )
                                }
                            }

                            when (val appendState = movies.loadState.append) {
                                is LoadState.Loading -> {
                                    item {
                                        SearchAppendLoadingState()
                                    }
                                }

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
    }
}

@Composable
private fun SearchResultsHeader(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = "Search Results",
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SearchPromptState(
    query: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = ReelsSpacing.medium),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(ReelsDimensions.stateCardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.stateCardContentSpacing)
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.stateIconContainerSize),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.MovieFilter,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.stateIconSize)
                    )
                }
            }

            Text(
                text = if (query.isBlank()) "Find your next movie" else "Keep typing",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (query.isBlank()) {
                    "Search by movie title, then save favorites for offline access."
                } else {
                    "Add one more character to search TMDB."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SearchResultLoadingState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultSpacing)
    ) {
        repeat(3) {
            SearchResultPlaceholderRow()
        }
    }
}

@Composable
private fun SearchResultPlaceholderRow(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(ReelsDimensions.searchResultCardPadding),
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultContentSpacing),
            verticalAlignment = Alignment.Top
        ) {
            Spacer(
                modifier = Modifier
                    .width(ReelsDimensions.searchResultPosterWidth)
                    .height(116.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
            ) {
                PlaceholderBar(width = 180.dp, height = 18.dp)
                PlaceholderBar(width = 118.dp, height = 14.dp)
                PlaceholderBar(width = 74.dp, height = 28.dp)

                Row(horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
                    PlaceholderBar(width = 74.dp, height = 28.dp)
                    PlaceholderBar(width = 94.dp, height = 28.dp)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderBar(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    )
}

@Composable
private fun SearchAppendLoadingState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.medium),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
    }
}

@Composable
private fun RecentSearchesSection(
    recentSearches: List<String>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchSectionSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Clear all",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onEvent(SearchEvent.ClearRecentSearchesClicked)
                }
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
        ) {
            recentSearches.forEach { query ->
                Surface(
                    onClick = {
                        onEvent(SearchEvent.RecentSearchClicked(query = query))
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    color = if (isReelsLightTheme()) {
                        ReelsLightGenreChipContainer
                    } else {
                        ReelsGenreChipContainer
                    },
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = ReelsDimensions.searchChipHorizontalPadding,
                            vertical = ReelsDimensions.searchChipVerticalPadding
                        ),
                        text = query,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    )
                }

            }
        }
    }
}

private const val MIN_SEARCH_QUERY_LENGTH = 2
