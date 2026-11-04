/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.account.usecase

import com.norbertotaveras.reels.domain.account.model.TmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.domain.account.repository.TmdbAccountRepository
import javax.inject.Inject

class DeleteTmdbMovieRatingUseCase @Inject constructor(
    private val repository: TmdbAccountRepository,
) {
    suspend operator fun invoke(
        movieId: Int,
    ): Result<TmdbMovieAccountStateDataModel> {
        return repository.deleteMovieRating(movieId = movieId)
    }
}
