/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.model.discover.DiscoverSort
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
internal fun DiscoverAppliedFiltersRow(
    state: DiscoverUiState,
    sortLabel: String,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters = state.filters

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(
                ReelsDimensions.discoverAppliedFilterSpacing
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                DiscoverSortSummaryChip(
                    sortLabel = sortLabel,
                    onClick = { onEvent(DiscoverEvent.FiltersClicked) }
                )
            }

            state.genreLabel(filters = filters)?.let { genreName ->
                item {
                    DiscoverRemovableFilterChip(
                        label = genreName,
                        onRemoveClick = {
                            onEvent(DiscoverEvent.GenreSelected(genreId = null))
                        }
                    )
                }
            }

            filters.minimumRating?.let { rating ->
                item {
                    DiscoverRemovableFilterChip(
                        label = "$rating+",
                        showRatingIcon = true,
                        onRemoveClick = {
                            onEvent(DiscoverEvent.RatingSelected(null))
                        }
                    )
                }
            }

            filters.releasePeriodDisplayLabel?.let { releasePeriodLabel ->
                item {
                    DiscoverRemovableFilterChip(
                        label = releasePeriodLabel,
                        onRemoveClick = {
                            onEvent(
                                DiscoverEvent.ReleasePeriodSelected(
                                    yearFrom = null,
                                    yearTo = null
                                )
                            )
                        }
                    )
                }
            }
        }

        if (state.hasActiveFilters) {
            Row(
                modifier = Modifier
                    .clickable(
                        role = Role.Button,
                        onClick = { onEvent(DiscoverEvent.ClearFiltersClicked) }
                    )
                    .padding(
                        horizontal = ReelsSpacing.xSmall,
                        vertical = ReelsDimensions.discoverClearFiltersVerticalPadding
                    ),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.discoverClearFiltersIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Clear all filters",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun DiscoverSortSummaryChip(
    sortLabel: String,
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
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.discoverSortHorizontalPadding,
                vertical = ReelsDimensions.discoverSortVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = sortLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
internal fun DiscoverActiveFiltersRow(
    state: DiscoverUiState,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DiscoverSectionTitle(title = "Active Filters")

            TextButton(
                onClick = {
                    onEvent(DiscoverEvent.ClearFiltersClicked)
                }
            ) {
                Text(text = "Clear all")
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
        ) {
            state.genreLabel(filters = state.filterSheetFilters)?.let { genreName ->
                DiscoverRemovableFilterChip(
                    label = genreName,
                    onRemoveClick = {
                        onEvent(
                            DiscoverEvent.GenreSelected(
                                genreId = null
                            )
                        )
                    }
                )
            }

            state.filterSheetFilters.releasePeriodDisplayLabel?.let { releasePeriodLabel ->
                DiscoverRemovableFilterChip(
                    label = releasePeriodLabel,
                    onRemoveClick = {
                        onEvent(
                            DiscoverEvent.ReleasePeriodSelected(
                                yearFrom = null,
                                yearTo = null
                            )
                        )
                    }
                )
            }

            state.filterSheetFilters.minimumRating?.let { rating ->
                DiscoverRemovableFilterChip(
                    label = "$rating+ rating",
                    onRemoveClick = {
                        onEvent(DiscoverEvent.RatingSelected(minimumRating = null))
                    }
                )
            }

            if (state.filterSheetFilters.sort != DiscoverSort.Popularity) {
                DiscoverRemovableFilterChip(
                    label = state.filterSheetFilters.sort.displayLabel,
                    onRemoveClick = {
                        onEvent(DiscoverEvent.SortSelected(sort = DiscoverSort.Popularity))
                    }
                )
            }
        }
    }
}

@Composable
private fun DiscoverRemovableFilterChip(
    label: String,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    showRatingIcon: Boolean = false,
) {
    Surface(
        modifier = modifier.clickable(
            role = Role.Button,
            onClick = onRemoveClick
        ),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
        contentColor = MaterialTheme.colorScheme.primary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.discoverFilterChipHorizontalPadding,
                vertical = ReelsDimensions.discoverFilterChipVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showRatingIcon) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.discoverAppliedFilterIconSize)
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.discoverAppliedFilterIconSize)
            )
        }
    }
}
