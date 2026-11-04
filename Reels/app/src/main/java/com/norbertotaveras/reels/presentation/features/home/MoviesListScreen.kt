/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel

@Composable
fun MoviesListScreen(
    popularMovies: LazyPagingItems<MovieDataModel>,
    nowPlayingMovies: LazyPagingItems<MovieDataModel>,
    upcomingMovies: LazyPagingItems<MovieDataModel>,
    topRatedMovies: LazyPagingItems<MovieDataModel>,
    onEvent: (MoviesListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MoviesListContent(
        popularMovies = popularMovies,
        nowPlayingMovies = nowPlayingMovies,
        upcomingMovies = upcomingMovies,
        topRatedMovies = topRatedMovies,
        onEvent = onEvent,
        modifier = modifier
    )
}
