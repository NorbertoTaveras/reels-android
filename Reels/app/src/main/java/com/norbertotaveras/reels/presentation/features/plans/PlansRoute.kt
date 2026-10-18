/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlansRoute(
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlansViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PlansScreen(
        state = state,
        onBackClick = onBackClick,
        onMovieClick = onMovieClick,
        onTabSelected = viewModel::selectTab,
        onReleaseRemove = viewModel::removeRelease,
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
