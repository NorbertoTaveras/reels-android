package com.norbertotaveras.reels.presentation.features.movies.category

import com.norbertotaveras.reels.domain.model.constant.MovieCategory

data class MovieCategoryUiState(
    val category: MovieCategory = MovieCategory.POPULAR,
    val language: String = "en-US",
)
