package com.norbertotaveras.reels.presentation.features.movies.list

sealed interface MoviesListEffect {
    data class NavigateToMovieDetails(val movieId: Int) : MoviesListEffect
}
