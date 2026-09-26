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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.norbertotaveras.reels.domain.model.discover.DiscoverSort
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatingContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.isReelsLightTheme

@Composable
fun DiscoverScreen(
    state: DiscoverUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            start = ReelsSpacing.screenHorizontal,
            top = ReelsDimensions.discoverScreenTopPadding,
            end = ReelsSpacing.screenHorizontal,
            bottom = ReelsDimensions.discoverScreenBottomPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverGridHorizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverGridVerticalSpacing)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            DiscoverHeader(
                hasActiveFilters = state.hasActiveFilters,
                movieCount = movies.itemCount,
                sortLabel = state.filters.sort.displayLabel,
                onFilterClick = {
                    onEvent(DiscoverEvent.FiltersClicked)
                }
            )
        }

        if (state.hasActiveFilters) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ActiveFiltersRow(
                    onClearClick = {
                        onEvent(DiscoverEvent.ClearFiltersClicked)
                    }
                )
            }
        }

        when (val refreshState = movies.loadState.refresh) {
            is LoadState.Loading -> {
                items(DISCOVER_PLACEHOLDER_COUNT) {
                    DiscoverPlaceholderCard()
                }
            }

            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    DiscoverErrorState(
                        message = refreshState.error.toUserMessage(
                            fallback = "Unable to discover movies."
                        ),
                        onRetry = movies::retry
                    )
                }
            }

            is LoadState.NotLoading -> {
                if (movies.itemCount == 0) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyState(
                            title = if (state.hasActiveFilters) {
                                "No matches found"
                            } else {
                                "No movies found"
                            },
                            message = if (state.hasActiveFilters) {
                                "This combination is a little too specific. Clear filters or widen your rating, year, or genre."
                            } else {
                                "Try refreshing Discover again in a moment."
                            }
                        )
                    }
                } else {
                    items(
                        count = movies.itemCount,
                        key = { index ->
                            val movie = movies.peek(index)
                            "${movie?.id ?: "placeholder"}-$index"
                        }
                    ) { index ->
                        val movie = movies[index] ?: return@items

                        DiscoverPosterCard(
                            movie = movie,
                            onClick = {
                                onEvent(DiscoverEvent.MovieClicked(movieId = movie.id))
                            }
                        )
                    }
                }
            }
        }

        when (val appendState = movies.loadState.append) {
            is LoadState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(ReelsSpacing.large),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    InlineRetryState(
                        message = appendState.error.toUserMessage(
                            fallback = "Unable to load more movies."
                        ),
                        retryLabel = "Retry",
                        onRetry = movies::retry
                    )
                }
            }

            is LoadState.NotLoading -> Unit
        }
    }

        if (state.isFilterSheetVisible) {
            DiscoverFilterSheet(
                state = state,
                movieCount = movies.itemCount,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun DiscoverHeader(
    hasActiveFilters: Boolean,
    movieCount: Int,
    sortLabel: String,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverHeaderSpacing)
            ) {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 34.sp,
                        lineHeight = 40.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = movieCount.toMovieCountLabel(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            DiscoverFilterButton(
                hasActiveFilters = hasActiveFilters,
                onClick = onFilterClick
            )
        }

        Surface(
            modifier = Modifier.clickable(onClick = onFilterClick),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
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
                    text = "Sort:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = sortLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DiscoverFilterButton(
    hasActiveFilters: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(44.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = if (hasActiveFilters) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (hasActiveFilters) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (hasActiveFilters) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            }
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Open discover filters",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiscoverFilterSheet(
    state: DiscoverUiState,
    movieCount: Int,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = null,
        onDismissRequest = {
            onEvent(DiscoverEvent.FiltersDismissed)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
        ) {
            DiscoverFilterSheetHeader(
                onResetClick = {
                    onEvent(DiscoverEvent.ClearFiltersClicked)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ReelsSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
            ) {
                if (state.hasActiveFilters) {
                    DiscoverActiveFiltersRow(
                        state = state,
                        onEvent = onEvent
                    )
                }

                DiscoverChipSection(title = "Genre") {
                    when {
                        state.isLoadingFilters -> {
                            repeat(10) {
                                DiscoverFilterSkeletonChip()
                            }
                        }

                        state.filterErrorMessage != null -> Unit

                        else -> {
                            state.genres
                                .toDiscoverFilterGenres()
                                .forEach { genre ->
                                DiscoverFilterChip(
                                    label = genre.displayName,
                                    selected = state.filters.genreId == genre.id,
                                    onClick = {
                                        onEvent(
                                            if (state.filters.genreId == genre.id) {
                                                DiscoverEvent.GenreSelected(
                                                    genreId = null,
                                                    genreName = null
                                                )
                                            } else {
                                                DiscoverEvent.GenreSelected(
                                                    genreId = genre.id,
                                                    genreName = genre.displayName
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
                    DISCOVER_YEARS.forEach { filter ->
                        DiscoverFilterChip(
                            label = filter.label,
                            selected = state.filters.year == filter.year,
                            onClick = {
                                onEvent(
                                    DiscoverEvent.YearSelected(
                                        year = filter.year.takeUnless {
                                            state.filters.year == filter.year
                                        }
                                    )
                                )
                            }
                        )
                    }
                }

                DiscoverChipSection(title = "Min Rating") {
                    DISCOVER_RATINGS.forEach { rating ->
                        DiscoverFilterChip(
                            label = rating?.let { "$it+" } ?: "Any",
                            selected = state.filters.minimumRating == rating,
                            onClick = {
                                onEvent(DiscoverEvent.RatingSelected(minimumRating = rating))
                            }
                        )
                    }
                }

                DiscoverSortSection(
                    selectedSort = state.filters.sort,
                    onSortSelected = { sort ->
                        onEvent(DiscoverEvent.SortSelected(sort = sort))
                    }
                )

                DiscoverLanguageRow(language = state.language)
            }

            DiscoverFilterSheetFooter(
                movieCount = movieCount,
                onApplyClick = {
                    onEvent(DiscoverEvent.FiltersDismissed)
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
                top = ReelsSpacing.large,
                end = ReelsSpacing.screenHorizontal
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(onClick = onResetClick) {
            Text(
                text = "Reset all",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ActiveFiltersRow(
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Filters active",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        FilledTonalButton(onClick = onClearClick) {
            Text(text = "Clear filters")
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
            state.filters.genreName?.let { genreName ->
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

            state.filters.year?.let { year ->
                DiscoverRemovableFilterChip(
                    label = year.toString(),
                    onRemoveClick = {
                        onEvent(DiscoverEvent.YearSelected(year = null))
                    }
                )
            }

            state.filters.minimumRating?.let { rating ->
                DiscoverRemovableFilterChip(
                    label = "$rating+ rating",
                    onRemoveClick = {
                        onEvent(DiscoverEvent.RatingSelected(minimumRating = null))
                    }
                )
            }

            if (state.filters.sort != DiscoverSort.Popularity) {
                DiscoverRemovableFilterChip(
                    label = state.filters.sort.displayLabel,
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
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        DiscoverSectionTitle(title = title)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverFilterRowSpacing),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
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
            fontSize = 9.sp,
            lineHeight = 12.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.ExtraBold
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
                horizontal = ReelsDimensions.discoverFilterChipHorizontalPadding,
                vertical = 7.dp
            ),
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                lineHeight = 13.sp
            ),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DiscoverFilterSkeletonChip(
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .width(86.dp)
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
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        lineHeight = 12.sp
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
                        fontSize = 11.sp,
                        lineHeight = 14.sp
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
    movieCount: Int,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                thickness = 1.dp
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal)
                    .padding(top = ReelsSpacing.medium),
                onClick = onApplyClick,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Show ${movieCount.toMovieCountLabel(capitalize = true)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DiscoverPosterCard(
    movie: MovieDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverPosterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.large)
        ) {
            AsyncImage(
                model = movie.posterPath.toTmdbImageUrl(),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            if (movie.posterPath.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Outlined.MovieFilter,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DiscoverRatingBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(ReelsDimensions.discoverRatingEdgePadding),
                rating = movie.voteAverage
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.releaseDate.toReleaseYear()?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DiscoverRatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = ReelsRatingContainer,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.discoverRatingHorizontalPadding,
                vertical = ReelsDimensions.discoverRatingVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.discoverRatingIconSize)
            )

            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DiscoverPlaceholderCard(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverPosterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.large)
                .background(discoverPlaceholderColor())
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .size(height = 18.dp, width = 1.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(discoverPlaceholderColor())
        )
    }
}

@Composable
private fun DiscoverErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(ReelsDimensions.stateCardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.stateCardContentSpacing)
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.stateIconContainerSize),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.stateIconSize)
                    )
                }
            }

            Text(
                text = message.toErrorTitle(fallback = "Unable to discover movies"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            FilledTonalButton(
                modifier = Modifier.padding(top = ReelsSpacing.small),
                onClick = onRetry
            ) {
                Text(text = "Try Again")
            }
        }
    }
}

@Composable
private fun discoverPlaceholderColor(): Color {
    return MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f)
}

private val DISCOVER_YEARS = listOf(
    DiscoverYearFilter(label = "2024", year = 2024),
    DiscoverYearFilter(label = "2023", year = 2023),
    DiscoverYearFilter(label = "2022", year = 2022),
    DiscoverYearFilter(label = "2021", year = 2021),
    DiscoverYearFilter(label = "2010s", year = 2010),
    DiscoverYearFilter(label = "2000s", year = 2000)
)
private val DISCOVER_RATINGS = listOf(null, 5, 6, 7, 8)
private val DISCOVER_GENRE_ORDER = listOf(
    "Action",
    "Adventure",
    "Animation",
    "Comedy",
    "Drama",
    "History",
    "Horror",
    "Romance",
    "Science Fiction",
    "Thriller"
)
private val DISCOVER_SORTS = listOf(
    DiscoverSort.Popularity,
    DiscoverSort.HighestRated,
    DiscoverSort.Newest,
    DiscoverSort.Alphabetical
)
private const val DISCOVER_PLACEHOLDER_COUNT = 12

private data class DiscoverYearFilter(
    val label: String,
    val year: Int,
)

private data class DiscoverGenreFilter(
    val id: Int,
    val displayName: String,
)

private val DiscoverSort.displayLabel: String
    get() = when (this) {
        DiscoverSort.Popularity -> "Popularity"
        DiscoverSort.HighestRated -> "Rating"
        DiscoverSort.Newest -> "Release Date"
        DiscoverSort.Alphabetical -> "A - Z"
        else -> label
    }

private val DiscoverSort.descriptionLabel: String
    get() = when (this) {
        DiscoverSort.Popularity -> "Most watched first"
        DiscoverSort.HighestRated -> "Highest rated first"
        DiscoverSort.Newest -> "Newest releases first"
        DiscoverSort.Alphabetical -> "Alphabetical order"
        DiscoverSort.Revenue -> "Highest box office first"
    }

private fun Int.toMovieCountLabel(capitalize: Boolean = false): String {
    return when {
        this == 1 && capitalize -> "1 Movie"
        this == 1 -> "1 movie"
        capitalize -> "$this Movies"
        else -> "$this movies"
    }
}

private fun String.toDiscoverLanguageLabel(): String {
    return when (this) {
        "en-US" -> "English · All regions"
        "en-GB" -> "English · United Kingdom"
        "es-ES" -> "Spanish · Spain"
        "fr-FR" -> "French · France"
        else -> this
    }
}

private fun List<GenreDataModel>.toDiscoverFilterGenres(): List<DiscoverGenreFilter> {
    return DISCOVER_GENRE_ORDER.mapNotNull { expectedName ->
        firstOrNull { genre -> genre.name == expectedName }?.let { genre ->
            DiscoverGenreFilter(
                id = genre.id,
                displayName = expectedName.toDiscoverGenreDisplayName()
            )
        }
    }
}

private fun String.toDiscoverGenreDisplayName(): String {
    return when (this) {
        "Science Fiction" -> "Sci-Fi"
        else -> this
    }
}

@Composable
private fun discoverFilterControlColor(): Color {
    return if (isReelsLightTheme()) {
        ReelsLightGenreChipContainer
    } else {
        ReelsGenreChipContainer.copy(alpha = 0.58f)
    }
}

private val DiscoverUiState.activeFilterCount: Int
    get() {
        var count = 0
        if (filters.genreId != null) count += 1
        if (filters.year != null) count += 1
        if (filters.minimumRating != null) count += 1
        if (filters.sort != DiscoverSort.Popularity) count += 1
        return count
    }
