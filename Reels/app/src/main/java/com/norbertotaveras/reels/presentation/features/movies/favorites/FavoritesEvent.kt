/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

sealed interface FavoritesEvent {
    data class MovieClicked(val movieId: Int) : FavoritesEvent
    data class FavoriteClicked(val movieId: Int) : FavoritesEvent
}
