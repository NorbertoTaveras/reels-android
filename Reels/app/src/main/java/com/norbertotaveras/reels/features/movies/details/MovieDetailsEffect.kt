package com.norbertotaveras.reels.features.movies.details

sealed interface MovieDetailsEffect {
    data object NavigateBack : MovieDetailsEffect
    data class NavigateToCastDetails(val personId: Int) : MovieDetailsEffect
    data class OpenTrailer(val url: String) : MovieDetailsEffect
    data class NavigateToMovieDetails(val movieId: Int) : MovieDetailsEffect
}
