package com.norbertotaveras.reels.features.cast.details

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CastDetailsRoute(
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CastDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CastDetailsEffect.NavigateBack -> onBackClick()
                is CastDetailsEffect.NavigateToMovieDetails -> {
                    onMovieClick(effect.movieId)
                }
            }
        }
    }

    CastDetailsScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}
