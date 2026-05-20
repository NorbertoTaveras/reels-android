package com.norbertotaveras.reels.features.movies.list

import com.norbertotaveras.reels.model.constant.MovieCategory

sealed interface MoviesListEvent {
    data class CategorySelected(val category: MovieCategory) : MoviesListEvent
    data class MovieClicked(val movieId: Int) : MoviesListEvent
}
