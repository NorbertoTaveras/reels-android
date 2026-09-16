package com.norbertotaveras.reels.presentation.features.movies.favorites

sealed interface FavoritesEvent {
    data class MovieClicked(val movieId: Int) : FavoritesEvent
    data class FavoriteClicked(val movieId: Int) : FavoritesEvent
}
