/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.filmography

sealed interface FilmographyEffect {
    data object NavigateBack : FilmographyEffect
    data class NavigateToMovieDetails(val movieId: Int) : FilmographyEffect
}
