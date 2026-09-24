/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.details

sealed interface CastDetailsEvent {
    data object BackClicked : CastDetailsEvent
    data object RetryClicked : CastDetailsEvent
    data object FavoriteClicked : CastDetailsEvent
    data object FilmographyClicked : CastDetailsEvent
    data class MovieClicked(val movieId: Int) : CastDetailsEvent
}
