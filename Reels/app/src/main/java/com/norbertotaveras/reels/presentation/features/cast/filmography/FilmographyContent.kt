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
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun FilmographyContent(
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
