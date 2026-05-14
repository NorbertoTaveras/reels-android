package com.norbertotaveras.reels.features.movies.search

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun SearchRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    SearchScreen(
        state = viewModel.state,
        movies = searchResults,
        onQueryChanged = viewModel::onQueryChanged,
        onClearQuery = viewModel::onClearQuery,
        onSearchSubmitted = viewModel::onSearchSubmitted,
        onRecentSearchClick = viewModel::onRecentSearchClicked,
        onClearRecentSearches = viewModel::onClearRecentSearches,
        onMovieClick = onMovieClick
    )
}
