package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    onOpenExternalUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.OpenExternalUrl -> {
                    onOpenExternalUrl(effect.url)
                }
            }
        }
    }

    SettingsScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}
