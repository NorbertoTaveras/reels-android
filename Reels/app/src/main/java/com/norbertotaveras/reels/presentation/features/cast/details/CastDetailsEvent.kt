package com.norbertotaveras.reels.presentation.features.cast.details

sealed interface CastDetailsEvent {
    data object BackClicked : CastDetailsEvent
    data object RetryClicked : CastDetailsEvent
    data object FavoriteClicked : CastDetailsEvent
    data class MovieClicked(val movieId: Int) : CastDetailsEvent
}
