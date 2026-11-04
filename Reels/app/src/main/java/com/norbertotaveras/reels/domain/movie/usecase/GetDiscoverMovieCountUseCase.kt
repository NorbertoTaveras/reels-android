/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.movie.usecase

import com.norbertotaveras.reels.domain.discover.model.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.movie.repository.MovieRepository
import javax.inject.Inject

class GetDiscoverMovieCountUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        filters: DiscoverFilterDataModel,
        language: String,
    ): Result<Int> {
        return repository.getDiscoverMovieCount(
            filters = filters,
            language = language
        )
    }
}
