package com.norbertotaveras.reels.presentation.features.movies.category

sealed interface MovieCategoryEffect {
    data object NavigateBack : MovieCategoryEffect
    data class NavigateToMovieDetails(val movieId: Int) : MovieCategoryEffect
}
