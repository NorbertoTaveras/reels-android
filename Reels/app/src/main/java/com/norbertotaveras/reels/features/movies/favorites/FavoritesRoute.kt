package com.norbertotaveras.reels.features.movies.favorites

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun FavoritesRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

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
        state = state,
        onEvent = viewModel::onEvent
    )
}
