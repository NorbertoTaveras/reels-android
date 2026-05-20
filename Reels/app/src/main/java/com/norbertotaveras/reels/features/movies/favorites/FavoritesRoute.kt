package com.norbertotaveras.reels.features.movies.favorites

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun FavoritesRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoritesEffect.NavigateToMovieDetails -> {
                    onMovieClick(effect.movieId)
                }
            }
        }
    }

    FavoritesScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent
    )
}
