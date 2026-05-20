package com.norbertotaveras.reels.features.movies.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.norbertotaveras.reels.model.data.favorite.FavoriteMovieDataModel

class FavoritesUiState {
    var movies by mutableStateOf<List<FavoriteMovieDataModel>>(emptyList())
}
