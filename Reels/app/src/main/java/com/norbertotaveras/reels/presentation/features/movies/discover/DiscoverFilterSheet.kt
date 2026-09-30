/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.discover.DiscoverSort
import com.norbertotaveras.reels.domain.model.discover.hasActiveFilters
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPosterRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

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
                .fillMaxHeight(0.78f)
        ) {
            DiscoverFilterSheetHeader(
                onResetClick = {
                    onEvent(DiscoverEvent.ResetDraftFiltersClicked)
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f),
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
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
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

                        state.filterErrorMessage != null -> Unit

                        else -> {
                            state.genres.forEach { genre ->
                                DiscoverFilterChip(
                                    label = genre.name,
                                    selected = state.filterSheetFilters.genreId == genre.id,
                                    onClick = {
                                        onEvent(
                                            if (state.filterSheetFilters.genreId == genre.id) {
                                                DiscoverEvent.GenreSelected(
                                                    genreId = null,
                                                    genreName = null
                                                )
                                            } else {
                                                    DiscoverEvent.GenreSelected(
                                                        genreId = genre.id,
                                                        genreName = genre.name
                                                    )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                if (state.filterErrorMessage != null) {
                    InlineRetryState(
                        message = state.filterErrorMessage,
                        retryLabel = "Reload genres",
                        onRetry = {
                            onEvent(DiscoverEvent.RetryFiltersClicked)
                        }
                    )
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
                                        yearTo = period.yearTo.takeUnless { isSelected },
                                        label = period.label.takeUnless { isSelected }
                                    )
                                )
                            }
                        )
                    }
                }

                DiscoverChipSection(title = "Minimum Rating") {
                    DISCOVER_RATINGS.forEach { rating ->
                        DiscoverFilterChip(
                            label = rating?.let { "$it+" } ?: "Any",
                            selected = state.filterSheetFilters.minimumRating == rating,
                            onClick = {
                                onEvent(DiscoverEvent.RatingSelected(minimumRating = rating))
                            }
                        )
                    }
                }

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
