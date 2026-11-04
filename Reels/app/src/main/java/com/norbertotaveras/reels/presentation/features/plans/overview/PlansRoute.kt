/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.overview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlansRoute(
    onBackClick: () -> Unit,
    onBrowseReleases: () -> Unit,
    onBrowseMovies: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlansViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PlansScreen(
        state = state,
        onBackClick = onBackClick,
        onBrowseReleases = onBrowseReleases,
        onBrowseMovies = onBrowseMovies,
        onMovieClick = onMovieClick,
        onTabSelected = viewModel::selectTab,
        onReleaseRemove = viewModel::removeRelease,
        onReleaseEdit = viewModel::editReleaseReminder,
        onClearPlanError = viewModel::clearPlanError,
        onMovieNightEdit = viewModel::editMovieNight,
        onMovieNightStart = viewModel::startMovieNight,
        onMovieNightPause = viewModel::pauseMovieNight,
        onMovieNightResume = viewModel::resumeMovieNight,
        onMovieNightFinish = viewModel::finishMovieNight,
        onMovieNightCancel = viewModel::cancelMovieNight,
        onMovieNightRemove = viewModel::removeMovieNight,
        onRefreshExactAlarmAccess = viewModel::refreshExactAlarmAccess,
        modifier = modifier,
    )
}
