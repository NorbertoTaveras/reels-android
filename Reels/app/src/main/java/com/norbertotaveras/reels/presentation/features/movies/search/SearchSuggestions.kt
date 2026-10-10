/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsMutedContainerColor

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
        Column(
            verticalArrangement = Arrangement.spacedBy(
                ReelsDimensions.searchRecentListSpacing
            )
        ) {
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
    val hoverInteractionSource = remember { MutableInteractionSource() }
    val isHovered by hoverInteractionSource.collectIsHoveredAsState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .hoverable(hoverInteractionSource)
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Remove $query from recent searches",
                        action = {
                            onRemoveClick()
                            true
                        }
                    )
                )
            }
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = reelsBorderColor()
        )
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = ReelsDimensions.searchRecentRowMinHeight)
                .padding(horizontal = ReelsDimensions.searchRecentRowHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(
                ReelsDimensions.searchRecentRowContentSpacing
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchRecentIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                modifier = Modifier.weight(1f),
                text = query,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Box(
                modifier = Modifier.size(ReelsDimensions.searchRecentRemoveButtonSize),
                contentAlignment = Alignment.Center
            ) {
                if (isHovered) {
                    Surface(
                        modifier = Modifier.matchParentSize(),
                        onClick = onRemoveClick,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = reelsMutedContainerColor(),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove $query from recent searches",
                                modifier = Modifier.size(
                                    ReelsDimensions.searchRecentRemoveIconSize
                                )
                            )
                        }
                    }
                }
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
            modifier = Modifier.padding(horizontal = ReelsSpacing.xSmall),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
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
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
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
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
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
        modifier = modifier.clickable(
            role = Role.Button,
            onClick = onClick
        ),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = reelsBorderColor()
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.searchChipHorizontalPadding,
                vertical = ReelsDimensions.searchChipVerticalPadding
            ),
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            fontWeight = FontWeight.Medium
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
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = reelsBorderColor()
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsSpacing.large,
                vertical = ReelsSpacing.medium
            ),
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
                        imageVector = Icons.Outlined.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
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
                modifier = Modifier.size(24.dp),
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
