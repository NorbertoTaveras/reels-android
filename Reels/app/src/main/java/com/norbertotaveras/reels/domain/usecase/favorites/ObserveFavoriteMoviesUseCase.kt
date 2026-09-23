/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteMoviesUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    operator fun invoke(): Flow<List<FavoriteMovieDataModel>> {
        return repository.observeFavoriteMovies()
    }
}
