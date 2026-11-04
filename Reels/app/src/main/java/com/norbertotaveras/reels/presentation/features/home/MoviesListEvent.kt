/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.home

import com.norbertotaveras.reels.domain.movie.model.MovieCategory

sealed interface MoviesListEvent {
    data class SeeAllClicked(val category: MovieCategory) : MoviesListEvent
    data class MovieClicked(val movieId: Int) : MoviesListEvent
}
