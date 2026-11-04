/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.discover.model.hasActiveFilters
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiscoverFilterSheet(
    state: DiscoverUiState,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(
            topStart = ReelsDimensions.discoverFilterSheetCornerSize,
            topEnd = ReelsDimensions.discoverFilterSheetCornerSize
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f))
            )
        },
        onDismissRequest = {
            onEvent(DiscoverEvent.FiltersDismissed)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(ReelsDimensions.DISCOVER_FILTER_SHEET_HEIGHT_FRACTION)
        ) {
            DiscoverFilterSheetHeader(
                onResetClick = {
                    onEvent(DiscoverEvent.ResetDraftFiltersClicked)
                }
            )

            HorizontalDivider(
                color = reelsBorderColor(),
                thickness = 1.dp
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = ReelsSpacing.screenHorizontal,
                        vertical = ReelsSpacing.large
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    ReelsDimensions.discoverFilterSheetSectionSpacing
                )
            ) {
                if (state.filterSheetFilters.hasActiveFilters) {
                    DiscoverActiveFiltersRow(
                        state = state,
                        onEvent = onEvent
                    )
                }

                DiscoverChipSection(
                    title = "Genre",
                    maxItemsInEachRow = DISCOVER_GENRE_COLUMNS
                ) {
                    when {
                        state.isLoadingFilters -> {
                            DISCOVER_FILTER_SKELETON_WIDTHS.forEach { width ->
                                DiscoverFilterSkeletonChip(width = width)
                            }
                        }

                        state.filterErrorMessage != null -> {
                            DiscoverGenreErrorState(
                                onRetry = {
                                    onEvent(DiscoverEvent.RetryFiltersClicked)
                                }
                            )
                        }

                        else -> {
                            state.genres.forEach { genre ->
                                DiscoverFilterChip(
                                    label = genre.name,
                                    selected = state.filterSheetFilters.genreId == genre.id,
                                    onClick = {
                                        onEvent(
                                            if (state.filterSheetFilters.genreId == genre.id) {
                                                DiscoverEvent.GenreSelected(
                                                    genreId = null
                                                )
                                            } else {
                                                DiscoverEvent.GenreSelected(
                                                    genreId = genre.id
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                DiscoverChipSection(title = "Release Year") {
                    discoverReleasePeriods().forEach { period ->
                        val isSelected = state.filterSheetFilters.releaseYearFrom == period.yearFrom &&
                            state.filterSheetFilters.releaseYearTo == period.yearTo
                        DiscoverFilterChip(
                            label = period.label,
                            selected = isSelected,
                            onClick = {
                                onEvent(
                                    DiscoverEvent.ReleasePeriodSelected(
                                        yearFrom = period.yearFrom.takeUnless { isSelected },
                                        yearTo = period.yearTo.takeUnless { isSelected }
                                    )
                                )
                            }
                        )
                    }
                }

                DiscoverRatingSection(
                    selectedRating = state.filterSheetFilters.minimumRating,
                    onRatingSelected = { rating ->
                        onEvent(DiscoverEvent.RatingSelected(minimumRating = rating))
                    }
                )

                DiscoverSortSection(
                    selectedSort = state.filterSheetFilters.sort,
                    onSortSelected = { sort ->
                        onEvent(DiscoverEvent.SortSelected(sort = sort))
                    }
                )

                DiscoverLanguageRow(language = state.language)
            }

            DiscoverFilterSheetFooter(
                movieCount = state.filterResultCount,
                isLoading = state.isLoadingFilters || state.isLoadingFilterResultCount,
                onApplyClick = {
                    onEvent(DiscoverEvent.ApplyFiltersClicked)
                }
            )
        }
    }
}
