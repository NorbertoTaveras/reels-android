/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.details

sealed interface MovieDetailsEffect {
    data object NavigateBack : MovieDetailsEffect
    data class NavigateToCastDetails(val personId: Int) : MovieDetailsEffect
    data class NavigateToMovieCast(val movieId: Int) : MovieDetailsEffect
    data class OpenTrailer(val url: String) : MovieDetailsEffect
    data class NavigateToMovieDetails(val movieId: Int) : MovieDetailsEffect
    data class ShowMessage(
        val message: String,
        val retryEvent: MovieDetailsEvent? = null,
    ) : MovieDetailsEffect
}
