package com.norbertotaveras.reels.features.movies.details

sealed interface MovieDetailsEvent {
    data object BackClicked : MovieDetailsEvent
    data object RetryClicked : MovieDetailsEvent
    data object FavoriteClicked : MovieDetailsEvent
    data class CastClicked(val personId: Int) : MovieDetailsEvent
    data object CastSeeAllClicked : MovieDetailsEvent
    data class TrailerClicked(val trailerId: String) : MovieDetailsEvent
    data class SimilarMovieClicked(val movieId: Int) : MovieDetailsEvent
}
