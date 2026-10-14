/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

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

@Composable
fun SettingsRoute(
    onNavigateToLibrary: () -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    tmdbAuthCallbackId: Long? = null,
    onTmdbAuthCallbackConsumed: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(tmdbAuthCallbackId) {
        tmdbAuthCallbackId?.let { callbackId ->
            viewModel.onEvent(SettingsEvent.TmdbAccountCallbackReceived)
            onTmdbAuthCallbackConsumed(callbackId)
        }
    }

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SettingsEffect.NavigateToLibrary -> {
                    onNavigateToLibrary()
                }

                is SettingsEffect.OpenExternalUrl -> {
                    onOpenExternalUrl(effect.url)
                }

                is SettingsEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        SettingsScreen(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.fillMaxSize()
        )

        ReelsSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
