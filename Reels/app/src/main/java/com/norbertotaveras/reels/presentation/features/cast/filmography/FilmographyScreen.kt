/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPosterRatingContainerColor

@Composable
fun FilmographyScreen(
    state: FilmographyUiState,
    onEvent: (FilmographyEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            start = ReelsSpacing.screenHorizontal,
            top = ReelsDimensions.filmographyScreenTopPadding,
            end = ReelsSpacing.screenHorizontal,
            bottom = ReelsDimensions.filmographyScreenBottomPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(
            ReelsDimensions.filmographyGridHorizontalSpacing
        ),
        verticalArrangement = Arrangement.spacedBy(
            ReelsDimensions.filmographyGridVerticalSpacing
        )
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            FilmographyHeader(
                personName = state.personName,
                creditCount = state.movies.size.takeIf {
                    !state.isLoading && state.errorMessage == null && it > 0
                },
                onBackClick = {
                    onEvent(FilmographyEvent.BackClicked)
                }
            )
        }

        if (state.errorMessage == null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                FilmographySortRow(
                    selectedSort = state.sort,
                    onSortClick = { sort ->
                        onEvent(FilmographyEvent.SortSelected(sort = sort))
                    }
                )
            }
        }

        when {
            state.isLoading -> {
                items(FILMOGRAPHY_PLACEHOLDER_COUNT) {
                    FilmographyPlaceholderCard()
                }
            }

            state.errorMessage != null -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FilmographyErrorState(
                        onRetry = {
                            onEvent(FilmographyEvent.RetryClicked)
                        }
                    )
                }
            }

            state.movies.isEmpty() -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FilmographyEmptyState(
                        personName = state.personName
                    )
                }
            }

            else -> {
                items(
                    count = state.movies.size,
                    key = { index -> "${state.movies[index].id}-$index" }
                ) { index ->
                    val movie = state.movies[index]

                    FilmographyPosterCard(
                        movie = movie,
                        onClick = {
                            onEvent(FilmographyEvent.MovieClicked(movieId = movie.id))
                        }
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    FilmographyEndCap(creditCount = state.movies.size)
                }
            }
        }
    }
}

@Composable
private fun FilmographyHeader(
    personName: String,
    creditCount: Int?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = ReelsDimensions.filmographyHeaderBottomPadding),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.filmographyHeaderSpacing)
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.filmographyBackButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(ReelsDimensions.filmographyBackIconSize)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(ReelsDimensions.filmographyHeaderTextSpacing)) {
            Text(
                text = "Filmography",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    lineHeight = 30.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = personName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            creditCount?.let { count ->
                FilmographyCountChip(
                    modifier = Modifier.padding(top = ReelsDimensions.filmographyHeaderCountSpacing),
                    creditCount = count
                )
            }
        }
    }
}

@Composable
private fun FilmographyCountChip(
    creditCount: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.filmographyCountHorizontalPadding,
                vertical = ReelsDimensions.filmographyCountVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Movie,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.filmographyCountIconSize)
            )

            Text(
                text = "$creditCount credits",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            )
        }
    }
}

@Composable
private fun FilmographySortRow(
    selectedSort: FilmographySort,
    onSortClick: (FilmographySort) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.filmographyFilterSpacing),
        contentPadding = PaddingValues(end = ReelsSpacing.screenHorizontal)
    ) {
        items(
            count = FilmographySort.entries.size,
            key = { index -> FilmographySort.entries[index].name }
        ) { index ->
            val sort = FilmographySort.entries[index]

            FilmographyFilterChip(
                label = sort.label,
                selected = selectedSort == sort,
                onClick = {
                    onSortClick(sort)
                }
            )
        }
    }
}

@Composable
private fun FilmographyFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            }
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.filmographyFilterHorizontalPadding,
                vertical = ReelsDimensions.filmographyFilterVerticalPadding
            ),
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FilmographyPosterCard(
    movie: PersonMovieCreditDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.filmographyPosterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = movie.posterPath.toTmdbImageUrl(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (movie.voteAverage > 0.0) {
                FilmographyRatingBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(ReelsDimensions.filmographyRatingEdgePadding),
                    rating = movie.voteAverage
                )
            }
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.releaseDate.toReleaseYear()?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FilmographyRatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = reelsPosterRatingContainerColor(),
        contentColor = reelsImageRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.filmographyRatingHorizontalPadding,
                vertical = ReelsDimensions.filmographyRatingVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.filmographyRatingIconSize)
            )

            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FilmographyPlaceholderCard(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.filmographyPosterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.medium)
                .background(filmographyPlaceholderColor())
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(12.dp)
                .clip(MaterialTheme.shapes.small)
                .background(filmographyPlaceholderColor())
        )

        Box(
            modifier = Modifier
                .width(36.dp)
                .height(10.dp)
                .clip(MaterialTheme.shapes.small)
                .background(filmographyPlaceholderColor())
        )
    }
}

@Composable
private fun FilmographyEndCap(
    creditCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        Box(
            modifier = Modifier
                .size(width = 32.dp, height = 1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f))
        )

        Text(
            text = "All $creditCount credits",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun filmographyPlaceholderColor(): Color {
    return MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f)
}

@Composable
private fun FilmographyErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilmographyMessageState(
        modifier = modifier,
        icon = Icons.Outlined.ErrorOutline,
        title = "Couldn't load credits",
        message = "Check your connection and try again.",
        onRetry = onRetry
    )
}

@Composable
private fun FilmographyEmptyState(
    personName: String,
    modifier: Modifier = Modifier,
) {
    FilmographyMessageState(
        modifier = modifier,
        icon = Icons.Outlined.Movie,
        title = "No credits found",
        message = "$personName doesn't have any movie credits yet."
    )
}

@Composable
private fun FilmographyMessageState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.stateCardContentSpacing)
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.stateIconContainerSize),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.stateIconSize)
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            onRetry?.let { retry ->
                Button(
                    modifier = Modifier.padding(top = ReelsSpacing.small),
                    onClick = retry
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(ReelsSpacing.small))

                    Text(text = "Retry")
                }
            }
        }
    }
}

private const val FILMOGRAPHY_PLACEHOLDER_COUNT = 12
