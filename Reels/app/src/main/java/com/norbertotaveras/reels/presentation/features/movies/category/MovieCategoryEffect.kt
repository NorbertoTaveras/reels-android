/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.category

sealed interface MovieCategoryEffect {
    data object NavigateBack : MovieCategoryEffect
    data class NavigateToMovieDetails(val movieId: Int) : MovieCategoryEffect
}
