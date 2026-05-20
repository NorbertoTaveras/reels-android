package com.norbertotaveras.reels.features.movies.list

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun MoviesListRoute(
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoviesListViewModel = hiltViewModel(),
) {
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
        state = viewModel.state,
        movies = viewModel.movies.collectAsLazyPagingItems(),
        onEvent = viewModel::onEvent
    )
}
