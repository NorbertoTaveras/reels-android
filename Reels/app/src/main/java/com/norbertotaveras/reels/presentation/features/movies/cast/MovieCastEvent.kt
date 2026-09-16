package com.norbertotaveras.reels.presentation.features.movies.cast

sealed interface MovieCastEvent {
    data object BackClicked : MovieCastEvent
    data object RetryClicked : MovieCastEvent
    data class CastClicked(val personId: Int) : MovieCastEvent
}
