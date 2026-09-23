/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel

data class FavoritesUiState(
    val movies: List<FavoriteMovieDataModel> = emptyList(),
    val removingMovieIds: Set<Int> = emptySet(),
)
