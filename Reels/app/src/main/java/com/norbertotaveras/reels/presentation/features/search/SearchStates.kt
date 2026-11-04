/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.components.common.MessageState
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun SearchResultsHeader(
    count: Int,
    query: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(bottom = ReelsSpacing.small),
        text = "$count ${if (count == 1) "result" else "results"} for \"$query\"",
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
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
    MessageState(
        modifier = modifier,
        icon = Icons.Filled.Search,
        title = "Find your next watch",
        message = "Search by title, director, or cast member."
    )
}

@Composable
internal fun SearchNoResultsState(
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessageState(
        modifier = modifier,
        icon = Icons.Filled.Search,
        title = "No results found",
        message = "Try a different title or keyword.",
        actionLabel = "Clear filters",
        onAction = onClearQuery,
        verticalPadding = ReelsDimensions.stateNoResultsVerticalPadding,
        actionHeight = ReelsDimensions.stateCompactActionHeight,
        actionHorizontalPadding = ReelsDimensions.stateCompactActionHorizontalPadding,
        actionVerticalPadding = ReelsDimensions.stateCompactActionVerticalPadding
    )
}

@Composable
internal fun SearchErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessageState(
        modifier = modifier,
        icon = Icons.Outlined.ErrorOutline,
        title = "Couldn't load movies",
        message = "Check your connection and try again.",
        actionLabel = "Retry",
        actionIcon = Icons.Outlined.Refresh,
        onAction = onRetry
    )
}
