/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
internal fun SearchNoResultsState(
    query: String,
    suggestions: List<String>,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.searchStateHorizontalPadding,
                vertical = ReelsDimensions.searchStateVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchStateIcon {
            Icon(
                imageVector = Icons.Outlined.Movie,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchStateIconSize)
            )
        }

        SearchStateText(
            title = "No results found",
            message = "No movies match \"$query\". Try a different title or keyword."
        )

        if (suggestions.isNotEmpty()) {
            SearchSuggestionsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ReelsDimensions.searchStateMessageToActionSpacing),
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
            .padding(
                horizontal = ReelsDimensions.searchStateHorizontalPadding,
                vertical = ReelsDimensions.searchStateVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchStateIcon {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchStateIconSize)
            )
        }

        SearchStateText(
            title = "Search unavailable",
            message = message
        )

        Button(
            modifier = Modifier
                .padding(top = ReelsDimensions.searchStateMessageToActionSpacing)
                .height(ReelsDimensions.searchStateActionHeight),
            onClick = onRetry,
            shape = RoundedCornerShape(ReelsDimensions.searchStateActionCornerSize),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            contentPadding = PaddingValues(
                horizontal = ReelsDimensions.searchStateActionHorizontalPadding,
                vertical = ReelsDimensions.searchStateActionVerticalPadding
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    ReelsDimensions.searchStateActionContentSpacing
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.searchStateActionIconSize)
                )
                Text(
                    text = "Try again",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    fontWeight = FontWeight.Medium
                )
            }
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
            .padding(
                horizontal = ReelsDimensions.searchStateHorizontalPadding,
                vertical = ReelsDimensions.searchStateVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchStateIcon(content = icon)
        SearchStateText(title = title, message = message)
    }
}

@Composable
private fun SearchStateText(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = ReelsDimensions.searchStateIconToTitleSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                lineHeight = 28.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(top = ReelsDimensions.searchStateTitleToMessageSpacing),
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 23.sp
            ),
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
        shape = RoundedCornerShape(ReelsDimensions.searchStateIconContainerCornerSize),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}
