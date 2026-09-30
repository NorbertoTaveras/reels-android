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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.discover.DiscoverSort
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiscoverAppliedFiltersRow(
    filters: DiscoverFilterDataModel,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filters.genreName?.let { genreName ->
            item {
                DiscoverRemovableFilterChip(
                    label = genreName,
                    onRemoveClick = {
                        onEvent(DiscoverEvent.GenreSelected(null, null))
                    }
                )
            }
        }

        filters.releasePeriodLabel?.let { releasePeriodLabel ->
            item {
                DiscoverRemovableFilterChip(
                    label = releasePeriodLabel,
                    onRemoveClick = {
                        onEvent(
                            DiscoverEvent.ReleasePeriodSelected(
                                yearFrom = null,
                                yearTo = null,
                                label = null
                            )
                        )
                    }
                )
            }
        }

        filters.minimumRating?.let { rating ->
            item {
                DiscoverRemovableFilterChip(
                    label = "$rating+ rating",
                    onRemoveClick = {
                        onEvent(DiscoverEvent.RatingSelected(null))
                    }
                )
            }
        }

        if (filters.sort != DiscoverSort.Popularity) {
            item {
                DiscoverRemovableFilterChip(
                    label = filters.sort.displayLabel,
                    onRemoveClick = {
                        onEvent(DiscoverEvent.SortSelected(DiscoverSort.Popularity))
                    }
                )
            }
        }

        item {
            TextButton(
                onClick = {
                    onEvent(DiscoverEvent.ResetDraftFiltersClicked)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(ReelsSpacing.xSmall))
                Text(
                    text = "Clear all filters",
                    style = MaterialTheme.typography.labelMedium
                )
            }
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
            state.filterSheetFilters.genreName?.let { genreName ->
                DiscoverRemovableFilterChip(
                    label = genreName,
                    onRemoveClick = {
                        onEvent(
                            DiscoverEvent.GenreSelected(
                                genreId = null,
                                genreName = null
                            )
                        )
                    }
                )
            }

            state.filterSheetFilters.releasePeriodLabel?.let { releasePeriodLabel ->
                DiscoverRemovableFilterChip(
                    label = releasePeriodLabel,
                    onRemoveClick = {
                        onEvent(
                            DiscoverEvent.ReleasePeriodSelected(
                                yearFrom = null,
                                yearTo = null,
                                label = null
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
) {
    Surface(
        modifier = modifier.clickable(onClick = onRemoveClick),
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
                horizontal = ReelsSpacing.medium,
                vertical = ReelsSpacing.small
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
