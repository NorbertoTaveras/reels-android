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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatingContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

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
                onBackClick = {
                    onEvent(FilmographyEvent.BackClicked)
                }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            FilmographySortRow(
                selectedSort = state.sort,
                onSortClick = { sort ->
                    onEvent(FilmographyEvent.SortSelected(sort = sort))
                }
            )
        }

        when {
            state.isLoading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = ReelsSpacing.xxLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            state.errorMessage != null -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FilmographyErrorState(
                        message = state.errorMessage,
                        onRetry = {
                            onEvent(FilmographyEvent.RetryClicked)
                        }
                    )
                }
            }

            state.movies.isEmpty() -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(
                        title = "No movie credits found",
                        message = "This person does not have available movie credits yet."
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
            }
        }
    }
}

@Composable
private fun FilmographyHeader(
    personName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = ReelsDimensions.filmographyHeaderBottomPadding),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.filmographyHeaderSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.filmographyBackButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
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

        Column(
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
        ) {
            Text(
                text = "Filmography",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = personName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                horizontal = ReelsDimensions.filmographyFilterHorizontalPadding,
                vertical = ReelsDimensions.filmographyFilterVerticalPadding
            ),
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
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
                .clip(MaterialTheme.shapes.large)
        ) {
            AsyncImage(
                model = movie.posterPath.toTmdbImageUrl(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            FilmographyRatingBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(ReelsDimensions.filmographyRatingEdgePadding),
                rating = movie.voteAverage
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.releaseDate.toReleaseYear()?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.bodyMedium,
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
        shape = MaterialTheme.shapes.extraLarge,
        color = ReelsRatingContainer,
        contentColor = MaterialTheme.colorScheme.primary
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
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FilmographyErrorState(
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
                text = message.toErrorTitle(fallback = "Unable to load filmography"),
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
