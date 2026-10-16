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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.model.discover.hasActiveFilters
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.LoadMoreErrorState
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
internal fun DiscoverContent(
    state: DiscoverUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refreshState = movies.loadState.refresh
    val hasCachedMovies = movies.itemCount > 0
    val isUsingCachedContent = refreshState is LoadState.Error && hasCachedMovies
    var isCachedNoticeDismissed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isUsingCachedContent) {
        if (!isUsingCachedContent) isCachedNoticeDismissed = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (isUsingCachedContent && !isCachedNoticeDismissed) {
                DiscoverCachedResultsBanner(
                    isOffline = !state.isOnline,
                    onDismiss = { isCachedNoticeDismissed = true }
                )
            }

            LazyVerticalGrid(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(
                    start = ReelsSpacing.screenHorizontal,
                    top = ReelsDimensions.discoverScreenTopPadding,
                    end = ReelsSpacing.screenHorizontal,
                    bottom = ReelsDimensions.discoverScreenBottomPadding
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    ReelsDimensions.discoverGridHorizontalSpacing
                ),
                verticalArrangement = Arrangement.spacedBy(
                    ReelsDimensions.discoverGridVerticalSpacing
                )
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    DiscoverHeader(
                        state = state,
                        hasActiveFilters = state.hasActiveFilters,
                        activeFilterCount = state.activeFilterCount,
                        subtitle = when {
                            refreshState is LoadState.Loading && !hasCachedMovies -> {
                                "Loading movies…"
                            }

                            hasCachedMovies -> movies.itemCount.toMovieCountLabel()
                            else -> "Browse & filter"
                        },
                        showSort = hasCachedMovies && refreshState !is LoadState.Loading,
                        sortLabel = state.filters.sort.displayLabel,
                        onFilterClick = {
                            onEvent(DiscoverEvent.FiltersClicked)
                        },
                        onEvent = onEvent
                    )
                }

                when {
                    refreshState is LoadState.Loading && !hasCachedMovies -> {
                        items(DISCOVER_PLACEHOLDER_COUNT) {
                            DiscoverPlaceholderCard()
                        }
                    }

                    refreshState is LoadState.Error && !hasCachedMovies -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            DiscoverErrorState(
                                onRetry = movies::retry
                            )
                        }
                    }

                    !hasCachedMovies -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            DiscoverEmptyState(
                                onClearFilters = {
                                    onEvent(DiscoverEvent.ClearFiltersClicked)
                                }
                            )
                        }
                    }

                    else -> {
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

                when (val appendState = movies.loadState.append) {
                    is LoadState.Loading -> {
                        items(DISCOVER_APPEND_PLACEHOLDER_COUNT) {
                            DiscoverPlaceholderCard()
                        }
                    }

                    is LoadState.Error -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            LoadMoreErrorState(onRetry = movies::retry)
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
    state: DiscoverUiState,
    hasActiveFilters: Boolean,
    activeFilterCount: Int,
    subtitle: String,
    showSort: Boolean,
    sortLabel: String,
    onFilterClick: () -> Unit,
    onEvent: (DiscoverEvent) -> Unit,
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
                        lineHeight = 36.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        lineHeight = 18.sp
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
            DiscoverAppliedFiltersRow(
                state = state,
                sortLabel = sortLabel,
                onEvent = onEvent
            )
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
    Box(modifier = modifier.size(ReelsDimensions.discoverFilterButtonContainerSize)) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(ReelsDimensions.discoverFilterButtonSize)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(ReelsDimensions.discoverFilterButtonCornerSize),
            color = if (hasActiveFilters) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (hasActiveFilters) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            border = BorderStroke(
                width = 1.dp,
                color = if (hasActiveFilters) {
                    MaterialTheme.colorScheme.primary
                } else {
                    reelsBorderColor()
                }
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Open discover filters",
                    modifier = Modifier.size(ReelsDimensions.discoverFilterButtonIconSize)
                )
            }
        }

        if (hasActiveFilters) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(ReelsDimensions.discoverFilterBadgeSize),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
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
