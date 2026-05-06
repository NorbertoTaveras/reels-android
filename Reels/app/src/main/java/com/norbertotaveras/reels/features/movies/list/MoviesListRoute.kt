package com.norbertotaveras.reels.features.movies.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun MoviesListRoute(
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoviesListViewModel = hiltViewModel(),
) {
    MoviesListScreen(
        modifier = modifier,
        state = viewModel.state,
        movies = viewModel.movies.collectAsLazyPagingItems(),
        onCategorySelected = viewModel::onCategorySelected,
        onMovieClick = onMovieClick
    )
}
