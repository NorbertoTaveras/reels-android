/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

sealed interface MovieDetailsEvent {
    data object BackClicked : MovieDetailsEvent
    data object RetryClicked : MovieDetailsEvent
    data object AccountStateRetryClicked : MovieDetailsEvent
    data object FavoriteClicked : MovieDetailsEvent
    data object WatchlistClicked : MovieDetailsEvent
    data object RatingClicked : MovieDetailsEvent
    data class RatingSelected(val rating: Double) : MovieDetailsEvent
    data object RatingSheetDismissed : MovieDetailsEvent
    data object RatingSubmitted : MovieDetailsEvent
    data object RatingCleared : MovieDetailsEvent
    data class CastClicked(val personId: Int) : MovieDetailsEvent
    data class CastSaveClicked(val personId: Int) : MovieDetailsEvent
    data object CastSeeAllClicked : MovieDetailsEvent
    data class TrailerClicked(val trailerId: String) : MovieDetailsEvent
    data class SimilarMovieClicked(val movieId: Int) : MovieDetailsEvent
}
