/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.isReelsLightTheme

@Composable
internal fun RecentSearchesSection(
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
internal fun TrendingSearchesSection(
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
internal fun SearchSuggestionsSection(
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
internal fun SearchCachedResultsBanner(
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
