package com.norbertotaveras.reels.presentation.features.movies.cast

sealed interface MovieCastEffect {
    data object NavigateBack : MovieCastEffect
    data class NavigateToCastDetails(val personId: Int) : MovieCastEffect
}
