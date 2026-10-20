/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.norbertotaveras.reels.presentation.features.plans.MoviePlansSheet
import com.norbertotaveras.reels.presentation.features.plans.MoviePlansViewModel
import com.norbertotaveras.reels.presentation.features.plans.scheduleConfirmationDetails
import com.norbertotaveras.reels.presentation.ui.components.common.ReelsSnackbarHost
import com.norbertotaveras.reels.presentation.ui.components.common.ReelsSnackbarVisuals
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MovieDetailsRoute(
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onCastSeeAllClick: (Int) -> Unit,
    onTrailerClick: (String) -> Unit,
    onSimilarMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    plansViewModel: MoviePlansViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isPlansSheetVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.effect.collectLatest { effect ->
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
                    val result = snackbarHostState.showSnackbar(
                        visuals = ReelsSnackbarVisuals(
                            message = effect.message,
                            actionLabel = effect.retryEvent?.let { "Retry" }
                        )
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        effect.retryEvent?.let(viewModel::onEvent)
                    }
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MovieDetailsScreen(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (state.isRatingSheetVisible || isPlansSheetVisible) {
                        Modifier.blur(12.dp)
                    } else {
                        Modifier
                    }
                ),
            state = state,
            onEvent = viewModel::onEvent,
            onPlanClick = { isPlansSheetVisible = true },
        )

        if (state.isRatingSheetVisible) {
            MovieRatingSheet(
                movieTitle = state.movie?.title.orEmpty(),
                selectedRating = state.selectedTmdbRating,
                isLoading = state.isAccountActionLoading,
                onRatingSelected = { rating ->
                    viewModel.onEvent(MovieDetailsEvent.RatingSelected(rating = rating))
                },
                onSubmitClick = {
                    viewModel.onEvent(MovieDetailsEvent.RatingSubmitted)
                },
                onClearClick = {
                    viewModel.onEvent(MovieDetailsEvent.RatingCleared)
                },
                onDismiss = {
                    viewModel.onEvent(MovieDetailsEvent.RatingSheetDismissed)
                }
            )
        }

        if (isPlansSheetVisible && state.movie != null) {
            MoviePlansSheet(
                movie = checkNotNull(state.movie),
                viewModel = plansViewModel,
                onDismiss = { isPlansSheetVisible = false },
                onMovieNightSaved = { plan ->
                    isPlansSheetVisible = false
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            visuals = ReelsSnackbarVisuals(
                                message = "Movie night scheduled!",
                                supportingText = plan.scheduleConfirmationDetails(),
                            )
                        )
                    }
                },
            )
        }

        ReelsSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
