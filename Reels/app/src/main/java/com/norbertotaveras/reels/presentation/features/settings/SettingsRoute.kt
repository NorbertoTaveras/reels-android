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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.norbertotaveras.reels.presentation.features.plans.openExactAlarmSettings
import com.norbertotaveras.reels.presentation.ui.components.common.ReelsSnackbarHost
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsRoute(
    onNavigateToLibrary: () -> Unit,
    onNavigateToPlans: () -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    tmdbAuthCallbackId: Long? = null,
    onTmdbAuthCallbackConsumed: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(SettingsEvent.ScreenResumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            viewModel.onEvent(SettingsEvent.ScreenResumed)
        }

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(tmdbAuthCallbackId) {
        tmdbAuthCallbackId?.let { callbackId ->
            viewModel.onEvent(SettingsEvent.TmdbAccountCallbackReceived)
            onTmdbAuthCallbackConsumed(callbackId)
        }
    }

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SettingsEffect.OpenNotificationSettings -> context.openReelsNotificationSettings()
                SettingsEffect.OpenLiveUpdateSettings -> context.openReelsLiveUpdateSettings()
                SettingsEffect.OpenPreciseReminderSettings -> context.openExactAlarmSettings()
                SettingsEffect.NavigateToLibrary -> {
                    onNavigateToLibrary()
                }

                SettingsEffect.NavigateToPlans -> onNavigateToPlans()

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
            modifier = Modifier.fillMaxSize(),
        )

        ReelsSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
