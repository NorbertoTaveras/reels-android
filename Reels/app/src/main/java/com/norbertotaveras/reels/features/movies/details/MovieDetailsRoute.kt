package com.norbertotaveras.reels.features.movies.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun MovieDetailsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    MovieDetailsScreen(
        modifier = modifier,
        state = viewModel.state,
        onBackClick = onBackClick,
        onRetry = viewModel::loadMovie
    )
}
