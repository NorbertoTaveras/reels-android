package com.norbertotaveras.reels.presentation.features.movies.category

sealed interface MovieCategoryEvent {
    data object BackClicked : MovieCategoryEvent
    data class MovieClicked(val movieId: Int) : MovieCategoryEvent
}
