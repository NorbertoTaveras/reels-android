/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.movie.usecase

import com.norbertotaveras.reels.domain.movie.model.MovieCreditsDataModel
import com.norbertotaveras.reels.domain.movie.repository.MovieRepository
import javax.inject.Inject

class GetMovieCreditsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        movieId: Int,
        language: String
    ): Result<MovieCreditsDataModel> {
        return repository.getMovieCredits(
            movieId = movieId,
            language = language
        )
    }
}
