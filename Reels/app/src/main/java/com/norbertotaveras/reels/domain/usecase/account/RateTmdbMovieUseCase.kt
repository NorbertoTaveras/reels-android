/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.account

import com.norbertotaveras.reels.domain.model.account.TmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.domain.repository.account.TmdbAccountRepository
import javax.inject.Inject

class RateTmdbMovieUseCase @Inject constructor(
    private val repository: TmdbAccountRepository,
) {
    suspend operator fun invoke(
        movieId: Int,
        rating: Double,
    ): Result<TmdbMovieAccountStateDataModel?> {
        return repository.rateMovie(
            movieId = movieId,
            rating = rating
        ).fold(
            onSuccess = { repository.getMovieAccountState(movieId = movieId) },
            onFailure = { error -> Result.failure(error) }
        )
    }
}
