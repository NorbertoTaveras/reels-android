package com.norbertotaveras.reels.features.movies.favorites

sealed interface FavoritesEffect {
    data class NavigateToMovieDetails(val movieId: Int) : FavoritesEffect
}
