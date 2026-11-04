/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun MoviesListRoute(
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoviesListViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MoviesListEffect.NavigateToMovieDetails -> {
                    onMovieClick(effect.movieId)
                }
                is MoviesListEffect.NavigateToMovieCategory -> {
                    onSeeAllClick(effect.categoryValue)
                }
            }
        }
    }

    MoviesListScreen(
        modifier = modifier,
        popularMovies = viewModel.popularMovies.collectAsLazyPagingItems(),
        nowPlayingMovies = viewModel.nowPlayingMovies.collectAsLazyPagingItems(),
        upcomingMovies = viewModel.upcomingMovies.collectAsLazyPagingItems(),
        topRatedMovies = viewModel.topRatedMovies.collectAsLazyPagingItems(),
        onEvent = viewModel::onEvent
    )
}
