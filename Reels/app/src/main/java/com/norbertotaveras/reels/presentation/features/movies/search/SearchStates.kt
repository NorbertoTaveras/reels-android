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
internal fun SearchResultsHeader(
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
internal fun SearchIdleContent(
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
internal fun SearchPromptState(modifier: Modifier = Modifier) {
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
internal fun SearchNoResultsState(
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
internal fun SearchErrorState(
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
internal fun SearchResultLoadingState(modifier: Modifier = Modifier) {
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
internal fun SearchAppendLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.medium),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
    }
}
