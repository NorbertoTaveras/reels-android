package com.norbertotaveras.reels.presentation.features.movies.favorites

sealed interface FavoritesEffect {
    data class NavigateToMovieDetails(val movieId: Int) : FavoritesEffect
    data class ShowMessage(val message: String) : FavoritesEffect
}
