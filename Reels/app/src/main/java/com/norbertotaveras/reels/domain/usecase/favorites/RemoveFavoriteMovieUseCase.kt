/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject

class RemoveFavoriteMovieUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return repository.removeFavoriteMovie(movieId = movieId)
    }
}
