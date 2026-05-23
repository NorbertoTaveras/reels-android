package com.norbertotaveras.reels.presentation.features.cast.details

sealed interface CastDetailsEffect {
    data object NavigateBack : CastDetailsEffect
    data class NavigateToMovieDetails(val movieId: Int) : CastDetailsEffect
    data class ShowMessage(val message: String) : CastDetailsEffect
}
