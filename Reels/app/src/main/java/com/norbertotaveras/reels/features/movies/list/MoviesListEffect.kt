package com.norbertotaveras.reels.features.movies.list

sealed interface MoviesListEffect {
    data class NavigateToMovieDetails(val movieId: Int) : MoviesListEffect
}
