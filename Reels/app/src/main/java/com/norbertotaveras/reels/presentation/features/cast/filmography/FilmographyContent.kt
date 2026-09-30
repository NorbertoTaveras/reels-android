/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

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
