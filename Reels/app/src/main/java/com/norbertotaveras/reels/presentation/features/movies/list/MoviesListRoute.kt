package com.norbertotaveras.reels.presentation.features.movies.list

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun MoviesListRoute(
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoviesListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MoviesListEffect.NavigateToMovieDetails -> {
                    onMovieClick(effect.movieId)
                }
            }
        }
    }

    MoviesListScreen(
        modifier = modifier,
        state = state,
        popularMovies = viewModel.popularMovies.collectAsLazyPagingItems(),
        nowPlayingMovies = viewModel.nowPlayingMovies.collectAsLazyPagingItems(),
        upcomingMovies = viewModel.upcomingMovies.collectAsLazyPagingItems(),
        topRatedMovies = viewModel.topRatedMovies.collectAsLazyPagingItems(),
        onEvent = viewModel::onEvent
    )
}
