package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MovieDetailsRoute(
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onCastSeeAllClick: (Int) -> Unit,
    onTrailerClick: (String) -> Unit,
    onSimilarMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MovieDetailsEffect.NavigateBack -> onBackClick()
                is MovieDetailsEffect.NavigateToCastDetails -> {
                    onCastClick(effect.personId)
                }
                is MovieDetailsEffect.NavigateToMovieCast -> {
                    onCastSeeAllClick(effect.movieId)
                }
                is MovieDetailsEffect.OpenTrailer -> {
                    onTrailerClick(effect.url)
                }
                is MovieDetailsEffect.NavigateToMovieDetails -> {
                    onSimilarMovieClick(effect.movieId)
                }
                is MovieDetailsEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MovieDetailsScreen(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onEvent = viewModel::onEvent
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
