package com.norbertotaveras.reels.presentation.features.settings

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.SingletonImageLoader

@Composable
fun SettingsRoute(
    onNavigateToFavorites: () -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, context) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SettingsEffect.NavigateToFavorites -> {
                    onNavigateToFavorites()
                }

                SettingsEffect.ClearImageCache -> {
                    val imageLoader = SingletonImageLoader.get(context)
                    imageLoader.memoryCache?.clear()
                    imageLoader.diskCache?.clear()
                    Toast.makeText(context, "Image cache cleared", Toast.LENGTH_SHORT).show()
                }

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
