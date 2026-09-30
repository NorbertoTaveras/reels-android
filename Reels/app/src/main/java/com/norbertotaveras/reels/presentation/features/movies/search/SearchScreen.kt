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
fun SearchScreen(
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

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        modifier = modifier.height(ReelsDimensions.searchFieldHeight),
        value = query,
        onValueChange = onQueryChanged,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            lineHeight = 18.sp
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        decorationBox = { innerTextField ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isFocused) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.58f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.52f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = ReelsDimensions.searchFieldHorizontalPadding,
                        end = if (query.isBlank()) {
                            ReelsDimensions.searchFieldHorizontalPadding
                        } else {
                            ReelsSpacing.xSmall
                        }
                    ),
                    horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.searchFieldIconSize),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isBlank()) {
                            Text(
                                text = "Search movies, directors...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }

                    if (query.isNotBlank()) {
                        IconButton(
                            modifier = Modifier.size(36.dp),
                            onClick = onClearClick
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear search",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun SearchResultsHeader(
    count: Int,
    query: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(bottom = ReelsSpacing.xSmall),
        text = "$count ${if (count == 1) "result" else "results"} for \"$query\"",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SearchIdleContent(
    recentSearches: List<String>,
    trendingSearches: List<String>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchSectionGroupSpacing)
    ) {
        if (recentSearches.isNotEmpty()) {
            RecentSearchesSection(recentSearches, onEvent)
        }
        if (trendingSearches.isNotEmpty()) {
            TrendingSearchesSection(
                suggestions = trendingSearches,
                onEvent = onEvent
            )
        }
        if (recentSearches.isEmpty() && trendingSearches.isEmpty()) {
            SearchEmptyState()
        }
    }
}

@Composable
private fun SearchEmptyState(modifier: Modifier = Modifier) {
    SearchCenteredState(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchStateIconSize)
            )
        },
        title = "Find your next watch",
        message = "Search by title, director, or cast member."
    )
}

@Composable
private fun SearchPromptState(modifier: Modifier = Modifier) {
    SearchCenteredState(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchStateIconSize)
            )
        },
        title = "Keep typing",
        message = "Enter at least two characters to search."
    )
}

@Composable
private fun SearchNoResultsState(
    query: String,
    suggestions: List<String>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchSectionGroupSpacing)
    ) {
        SearchCenteredState(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.searchStateIconSize)
                )
            },
            title = "No results found",
            message = "No movies match \"$query\". Try a different title or keyword."
        )
        if (suggestions.isNotEmpty()) {
            SearchSuggestionsSection(
                title = "TRY INSTEAD",
                suggestions = suggestions,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun SearchErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = ReelsDimensions.searchStateTopPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        SearchStateIcon {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchStateIconSize)
            )
        }
        Text(
            text = "Search unavailable",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, lineHeight = 20.sp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            modifier = Modifier.padding(horizontal = ReelsSpacing.xLarge),
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(ReelsSpacing.small))
            Text(text = "Try again")
        }
    }
}

@Composable
private fun SearchCenteredState(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = ReelsDimensions.searchStateTopPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        SearchStateIcon(content = icon)
        Spacer(modifier = Modifier.height(ReelsSpacing.xSmall))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, lineHeight = 20.sp),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchStateIcon(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.size(ReelsDimensions.searchStateIconContainerSize),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
private fun SearchResultLoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultSpacing)
    ) {
        PlaceholderBar(width = 132.dp, height = 12.dp)
        repeat(4) { SearchResultPlaceholderRow() }
    }
}

@Composable
private fun SearchResultPlaceholderRow(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.50f)
        )
    ) {
        Row(
            modifier = Modifier.padding(ReelsDimensions.searchResultCardPadding),
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultCardPadding),
            verticalAlignment = Alignment.Top
        ) {
            Spacer(
                modifier = Modifier
                    .width(ReelsDimensions.searchResultPosterWidth)
                    .height(ReelsDimensions.searchResultPosterHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
            ) {
                PlaceholderBar(width = 150.dp, height = 14.dp)
                PlaceholderBar(width = 128.dp, height = 11.dp)
                PlaceholderBar(width = 48.dp, height = 22.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                    PlaceholderBar(width = 52.dp, height = 20.dp)
                    PlaceholderBar(width = 62.dp, height = 20.dp)
                    PlaceholderBar(width = 56.dp, height = 20.dp)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderBar(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
private fun SearchAppendLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.medium),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
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
        SearchSectionHeader(
            title = "RECENT",
            actionLabel = "Clear all",
            onActionClick = { onEvent(SearchEvent.ClearRecentSearchesClicked) }
        )
        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
            recentSearches.forEach { query ->
                RecentSearchRow(
                    query = query,
                    onClick = { onEvent(SearchEvent.RecentSearchClicked(query)) },
                    onRemoveClick = { onEvent(SearchEvent.RecentSearchRemoved(query)) }
                )
            }
        }
    }
}

@Composable
private fun RecentSearchRow(
    query: String,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.50f)
        )
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = ReelsDimensions.searchRecentRowMinHeight)
                .padding(start = ReelsDimensions.searchRecentRowHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchRecentIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                modifier = Modifier.weight(1f),
                text = query,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            IconButton(
                modifier = Modifier.size(36.dp),
                onClick = onRemoveClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove $query from recent searches",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TrendingSearchesSection(
    suggestions: List<String>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchSuggestionsSection(
        modifier = modifier,
        title = "TRENDING",
        suggestions = suggestions,
        showTrendingIcon = true,
        onEvent = onEvent
    )
}

@Composable
private fun SearchSuggestionsSection(
    title: String,
    suggestions: List<String>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
    showTrendingIcon: Boolean = false,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchSectionSpacing)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showTrendingIcon) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 0.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
        FlowRow(
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
        ) {
            suggestions.forEach { query ->
                SearchSuggestionChip(
                    label = query,
                    onClick = { onEvent(SearchEvent.RecentSearchClicked(query)) }
                )
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                lineHeight = 14.sp,
                letterSpacing = 0.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            modifier = Modifier.clickable(onClick = onActionClick),
            text = actionLabel,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, lineHeight = 16.sp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SearchSuggestionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = if (isReelsLightTheme()) ReelsLightGenreChipContainer else ReelsGenreChipContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.50f)
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.searchChipHorizontalPadding,
                vertical = ReelsDimensions.searchChipVerticalPadding
            ),
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, lineHeight = 15.sp)
        )
    }
}

@Composable
private fun SearchCachedResultsBanner(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.50f)
        )
    ) {
        Row(
            modifier = Modifier.padding(start = ReelsSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f),
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = ReelsSpacing.small),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Showing cached results",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp, lineHeight = 15.sp),
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Reconnect to refresh",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                modifier = Modifier.size(36.dp),
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Dismiss cached results notice",
                    modifier = Modifier.size(15.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
