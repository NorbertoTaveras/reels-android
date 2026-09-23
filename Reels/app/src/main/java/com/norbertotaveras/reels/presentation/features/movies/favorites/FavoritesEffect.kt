/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

sealed interface FavoritesEffect {
    data class NavigateToMovieDetails(val movieId: Int) : FavoritesEffect
    data class ShowMessage(val message: String) : FavoritesEffect
}
