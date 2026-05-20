package com.norbertotaveras.reels.features.movies.search

sealed interface SearchEffect {
    data class NavigateToMovieDetails(val movieId: Int) : SearchEffect
}
