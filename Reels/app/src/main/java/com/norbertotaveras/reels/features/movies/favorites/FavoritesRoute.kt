package com.norbertotaveras.reels.features.movies.favorites

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun FavoritesRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    FavoritesScreen(
        state = viewModel.state,
        onMovieClick = onMovieClick
    )
}
