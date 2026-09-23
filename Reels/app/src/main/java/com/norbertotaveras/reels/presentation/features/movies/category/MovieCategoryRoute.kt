/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun MovieCategoryRoute(
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieCategoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val movies = viewModel.movies.collectAsLazyPagingItems()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MovieCategoryEffect.NavigateBack -> onBackClick()
                is MovieCategoryEffect.NavigateToMovieDetails -> {
                    onMovieClick(effect.movieId)
                }
            }
        }
    }

    MovieCategoryScreen(
        modifier = modifier,
        state = state,
        movies = movies,
        onEvent = viewModel::onEvent
    )
}
