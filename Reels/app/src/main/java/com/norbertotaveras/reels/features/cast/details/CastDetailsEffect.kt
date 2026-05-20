package com.norbertotaveras.reels.features.cast.details

sealed interface CastDetailsEffect {
    data object NavigateBack : CastDetailsEffect
    data class NavigateToMovieDetails(val movieId: Int) : CastDetailsEffect
}
