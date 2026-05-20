package com.norbertotaveras.reels.features.movies.search

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun SearchRoute(
    onMovieClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToMovieDetails -> {
                    onMovieClick(effect.movieId)
                }
            }
        }
    }

    SearchScreen(
        state = state,
        movies = searchResults,
        onEvent = viewModel::onEvent
    )
}
