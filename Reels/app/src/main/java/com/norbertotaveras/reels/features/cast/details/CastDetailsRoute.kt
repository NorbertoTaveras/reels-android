package com.norbertotaveras.reels.features.cast.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CastDetailsRoute(
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CastDetailsViewModel = hiltViewModel(),
) {
    CastDetailsScreen(
        state = viewModel.state,
        onBackClick = onBackClick,
        onMovieClick = onMovieClick,
        onRetry = viewModel::loadPerson,
        modifier = modifier
    )
}
