package com.norbertotaveras.reels.presentation.features.movies.favorites

import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel

data class FavoritesUiState(
    val movies: List<FavoriteMovieDataModel> = emptyList(),
    val removingMovieIds: Set<Int> = emptySet(),
)
