package com.norbertotaveras.reels.features.movies.favorites

sealed interface FavoritesEvent {
    data class MovieClicked(val movieId: Int) : FavoritesEvent
}
