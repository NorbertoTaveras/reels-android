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

@Composable
internal fun DiscoverContent(
    state: DiscoverUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refreshState = movies.loadState.refresh

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
                activeFilterCount = state.activeFilterCount,
                subtitle = if (refreshState is LoadState.NotLoading && movies.itemCount > 0) {
                    movies.itemCount.toMovieCountLabel()
                } else {
                    "Browse & filter"
                },
                showSort = refreshState is LoadState.NotLoading && movies.itemCount > 0,
                sortLabel = state.filters.sort.displayLabel,
                onFilterClick = {
                    onEvent(DiscoverEvent.FiltersClicked)
                }
            )
        }

        if (state.hasActiveFilters) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DiscoverAppliedFiltersRow(
                    filters = state.filters,
                    onEvent = onEvent
                )
            }
        }

        when (refreshState) {
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
                        DiscoverEmptyState(
                            hasActiveFilters = state.hasActiveFilters,
                            onAdjustFilters = {
                                onEvent(DiscoverEvent.FiltersClicked)
                            },
                            onClearFilters = {
                                onEvent(DiscoverEvent.ClearFiltersClicked)
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
                items(DISCOVER_APPEND_PLACEHOLDER_COUNT) {
                    DiscoverPlaceholderCard()
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

            is LoadState.NotLoading -> {
                if (appendState.endOfPaginationReached && movies.itemCount > 0) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        DiscoverEndOfList(movieCount = movies.itemCount)
                    }
                }
            }
        }
    }

        if (state.isFilterSheetVisible) {
            DiscoverFilterSheet(
                state = state,
                onEvent = onEvent
            )
        }
    }
}
@Composable
private fun DiscoverHeader(
    hasActiveFilters: Boolean,
    activeFilterCount: Int,
    subtitle: String,
    showSort: Boolean,
    sortLabel: String,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
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
                        fontSize = 24.sp,
                        lineHeight = 30.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            DiscoverFilterButton(
                hasActiveFilters = hasActiveFilters,
                activeFilterCount = activeFilterCount,
                onClick = onFilterClick
            )
        }

        if (showSort) {
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
}

@Composable
private fun DiscoverFilterButton(
    hasActiveFilters: Boolean,
    activeFilterCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(44.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
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

            if (hasActiveFilters) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(17.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = activeFilterCount.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                lineHeight = 10.sp
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
