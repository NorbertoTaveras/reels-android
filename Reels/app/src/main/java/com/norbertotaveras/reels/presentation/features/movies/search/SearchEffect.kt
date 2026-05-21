package com.norbertotaveras.reels.presentation.features.movies.search

sealed interface SearchEffect {
    data class NavigateToMovieDetails(val movieId: Int) : SearchEffect
}
