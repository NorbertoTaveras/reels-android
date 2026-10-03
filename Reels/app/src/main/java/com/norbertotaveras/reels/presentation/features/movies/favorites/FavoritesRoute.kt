/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.norbertotaveras.reels.presentation.ui.components.common.ReelsSnackbarHost

@Composable
fun FavoritesRoute(
    onMovieClick: (Int) -> Unit,
    onPersonClick: (Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tmdbWatchlistMovies = viewModel.tmdbWatchlistMovies.collectAsLazyPagingItems()
    val tmdbRatedMovies = viewModel.tmdbRatedMovies.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(
        lifecycleOwner,
        tmdbWatchlistMovies,
        tmdbRatedMovies
    ) {
        fun refreshTmdbLibrary() {
            tmdbWatchlistMovies.refresh()
            tmdbRatedMovies.refresh()
        }

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTmdbLibrary()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            refreshTmdbLibrary()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoritesEffect.NavigateToMovieDetails -> {
                    onMovieClick(effect.movieId)
                }
                is FavoritesEffect.NavigateToCastDetails -> {
                    onPersonClick(effect.personId)
                }
                FavoritesEffect.NavigateToSettings -> {
                    onNavigateToSettings()
                }
                is FavoritesEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        FavoritesScreen(
            state = state,
            tmdbWatchlistMovies = tmdbWatchlistMovies,
            tmdbRatedMovies = tmdbRatedMovies,
            onEvent = viewModel::onEvent,
            modifier = Modifier.fillMaxSize()
        )

        ReelsSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
