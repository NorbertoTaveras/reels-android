/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject

class ToggleFavoriteMovieUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    suspend operator fun invoke(movie: MovieDetailsDataModel): Result<Unit> {
        return runCatching {
            repository.toggleFavoriteMovie(movie = movie)
        }
    }
}
