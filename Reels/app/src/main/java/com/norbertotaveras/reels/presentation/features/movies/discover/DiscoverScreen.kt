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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatingContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
fun DiscoverScreen(
    state: DiscoverUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier
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
            DiscoverHeader()
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            DiscoverFilters(
                state = state,
                onEvent = onEvent
            )
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
                            title = "No matches found",
                            message = "Adjust your filters to discover more movies."
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
}

@Composable
private fun DiscoverHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
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
            text = "Find movies by mood, genre, year, and rating",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DiscoverFilters(
    state: DiscoverUiState,
    onEvent: (DiscoverEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverSectionSpacing)
    ) {
        DiscoverChipSection(title = "Sort") {
            DiscoverSort.entries.forEach { sort ->
                DiscoverFilterChip(
                    label = sort.label,
                    selected = state.filters.sort == sort,
                    onClick = {
                        onEvent(DiscoverEvent.SortSelected(sort = sort))
                    }
                )
            }
        }

        DiscoverChipSection(title = "Genre") {
            DiscoverFilterChip(
                label = "All",
                selected = state.filters.genreId == null,
                onClick = {
                    onEvent(
                        DiscoverEvent.GenreSelected(
                            genreId = null,
                            genreName = null
                        )
                    )
                }
            )

            state.genres.forEach { genre ->
                DiscoverFilterChip(
                    label = genre.name,
                    selected = state.filters.genreId == genre.id,
                    onClick = {
                        onEvent(
                            DiscoverEvent.GenreSelected(
                                genreId = genre.id,
                                genreName = genre.name
                            )
                        )
                    }
                )
            }
        }

        DiscoverChipSection(title = "Year") {
            DISCOVER_YEARS.forEach { year ->
                DiscoverFilterChip(
                    label = year?.toString() ?: "Any",
                    selected = state.filters.year == year,
                    onClick = {
                        onEvent(DiscoverEvent.YearSelected(year = year))
                    }
                )
            }
        }

        DiscoverChipSection(title = "Rating") {
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

        if (state.filterErrorMessage != null) {
            InlineRetryState(
                message = state.filterErrorMessage,
                retryLabel = "Reload filters",
                onRetry = {
                    onEvent(DiscoverEvent.RetryFiltersClicked)
                }
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
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverFilterRowSpacing),
            contentPadding = PaddingValues(end = ReelsSpacing.screenHorizontal)
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        ReelsDimensions.discoverFilterRowSpacing
                    )
                ) {
                    content()
                }
            }
        }
    }
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
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                Color.Transparent
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
            }
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.discoverFilterChipHorizontalPadding,
                vertical = ReelsDimensions.discoverFilterChipVerticalPadding
            ),
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            DiscoverRatingBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(ReelsDimensions.discoverRatingEdgePadding),
                rating = movie.voteAverage
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 17.sp,
                lineHeight = 22.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.releaseDate.toReleaseYear()?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp
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
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 20.sp
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

private val DISCOVER_YEARS = listOf(null, 2026, 2025, 2024, 2023, 2022, 2021)
private val DISCOVER_RATINGS = listOf(null, 9, 8, 7, 6)
private const val DISCOVER_PLACEHOLDER_COUNT = 12
