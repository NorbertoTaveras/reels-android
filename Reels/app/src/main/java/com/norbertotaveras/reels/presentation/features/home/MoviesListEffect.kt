/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.home

sealed interface MoviesListEffect {
    data class NavigateToMovieDetails(val movieId: Int) : MoviesListEffect
    data class NavigateToMovieCategory(val categoryValue: String) : MoviesListEffect
}
