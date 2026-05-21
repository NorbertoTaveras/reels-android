package com.norbertotaveras.reels.presentation.features.movies.list

import com.norbertotaveras.reels.domain.model.constant.MovieCategory

data class MoviesListState(
    val category: MovieCategory = MovieCategory.POPULAR,
    val language: String = "en-US"
)
