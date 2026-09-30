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

@Composable
private fun DiscoverFilterSheetHeader(
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = ReelsSpacing.screenHorizontal,
                end = ReelsSpacing.screenHorizontal,
                top = ReelsSpacing.medium,
                bottom = ReelsSpacing.medium
            )
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(onClick = onResetClick) {
            Text(
                text = "Reset all",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

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
private fun DiscoverActiveFiltersRow(
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

@Composable
private fun DiscoverChipSection(
    title: String,
    modifier: Modifier = Modifier,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        DiscoverSectionTitle(title = title)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverFilterRowSpacing),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            maxItemsInEachRow = maxItemsInEachRow
        ) {
            content()
        }
    }
}

@Composable
private fun DiscoverSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun DiscoverFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        } else {
            discoverFilterControlColor()
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
            }
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp
            ),
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DiscoverFilterSkeletonChip(
    width: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .width(width)
            .height(36.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    )
}

@Composable
private fun DiscoverSortSection(
    selectedSort: DiscoverSort,
    onSortSelected: (DiscoverSort) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        DiscoverSectionTitle(title = "Sort By")

        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
            DISCOVER_SORTS.forEach { sort ->
                DiscoverSortRow(
                    label = sort.displayLabel,
                    description = sort.descriptionLabel,
                    selected = selectedSort == sort,
                    onClick = {
                        onSortSelected(sort)
                    }
                )
            }
        }
    }
}

@Composable
private fun DiscoverSortRow(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        } else {
            discoverFilterControlColor()
        },
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsSpacing.medium,
                vertical = ReelsSpacing.small
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                modifier = Modifier.size(18.dp),
                shape = CircleShape,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                    }
                ),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                if (selected) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverLanguageRow(
    language: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        DiscoverSectionTitle(title = "Language & Region")

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = discoverFilterControlColor(),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = ReelsSpacing.medium,
                    vertical = ReelsSpacing.small
                ),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = language.toDiscoverLanguageLabel(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DiscoverFilterSheetFooter(
    movieCount: Int?,
    isLoading: Boolean,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f),
                thickness = 1.dp
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ReelsSpacing.screenHorizontal,
                        vertical = ReelsSpacing.large
                    )
                    .height(48.dp),
                onClick = onApplyClick,
                enabled = !isLoading,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(ReelsSpacing.small))
                }

                Text(
                    text = if (isLoading) {
                        "Checking movies…"
                    } else {
                        movieCount?.let { count ->
                            "Show ${count.toMovieCountLabel(capitalize = true)}"
                        } ?: "Show Movies"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
