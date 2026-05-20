package com.norbertotaveras.reels.features.movies.favorites

import com.norbertotaveras.reels.model.data.favorite.FavoriteMovieDataModel

data class FavoritesUiState(
    val movies: List<FavoriteMovieDataModel> = emptyList()
)
