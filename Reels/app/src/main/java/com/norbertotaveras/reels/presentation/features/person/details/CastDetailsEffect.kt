/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.details

sealed interface CastDetailsEffect {
    data object NavigateBack : CastDetailsEffect
    data class NavigateToFilmography(val personId: Int) : CastDetailsEffect
    data class NavigateToMovieDetails(val movieId: Int) : CastDetailsEffect
    data class ShowMessage(val message: String) : CastDetailsEffect
}
