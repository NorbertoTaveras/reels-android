package com.norbertotaveras.reels.features.movies.list

import com.norbertotaveras.reels.model.constant.MovieCategory

data class MoviesListState(
    val category: MovieCategory = MovieCategory.POPULAR,
    val language: String = "en-US"
)
