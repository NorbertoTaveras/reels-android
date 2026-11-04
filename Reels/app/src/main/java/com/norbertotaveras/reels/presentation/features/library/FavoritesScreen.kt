/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    tmdbWatchlistMovies: LazyPagingItems<MovieDataModel>,
    tmdbRatedMovies: LazyPagingItems<MovieDataModel>,
    onEvent: (FavoritesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    FavoritesContent(
        state = state,
        tmdbWatchlistMovies = tmdbWatchlistMovies,
        tmdbRatedMovies = tmdbRatedMovies,
        onEvent = onEvent,
        modifier = modifier
    )
}
