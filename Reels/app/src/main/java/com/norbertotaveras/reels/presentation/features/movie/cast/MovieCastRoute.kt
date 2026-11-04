/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.cast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.norbertotaveras.reels.presentation.ui.components.common.ReelsSnackbarHost
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MovieCastRoute(
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieCastViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                MovieCastEffect.NavigateBack -> onBackClick()
                is MovieCastEffect.NavigateToCastDetails -> {
                    onCastClick(effect.personId)
                }
                is MovieCastEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MovieCastScreen(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onEvent = viewModel::onEvent
        )

        ReelsSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
