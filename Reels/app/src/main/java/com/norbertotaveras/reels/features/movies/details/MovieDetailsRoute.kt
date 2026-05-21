package com.norbertotaveras.reels.features.movies.details

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun MovieDetailsRoute(
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onTrailerClick: (String) -> Unit,
    onSimilarMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MovieDetailsEffect.NavigateBack -> onBackClick()
                is MovieDetailsEffect.NavigateToCastDetails -> {
                    onCastClick(effect.personId)
                }
                is MovieDetailsEffect.OpenTrailer -> {
                    onTrailerClick(effect.url)
                }
                is MovieDetailsEffect.NavigateToMovieDetails -> {
                    onSimilarMovieClick(effect.movieId)
                }
            }
        }
    }

    MovieDetailsScreen(
        modifier = modifier,
        state = state,
        onEvent = viewModel::onEvent
    )
}
