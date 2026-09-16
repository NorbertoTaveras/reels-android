package com.norbertotaveras.reels.presentation.features.movies.cast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MovieCastRoute(
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieCastViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MovieCastEffect.NavigateBack -> onBackClick()
                is MovieCastEffect.NavigateToCastDetails -> {
                    onCastClick(effect.personId)
                }
            }
        }
    }

    MovieCastScreen(
        modifier = modifier,
        state = state,
        onEvent = viewModel::onEvent
    )
}
