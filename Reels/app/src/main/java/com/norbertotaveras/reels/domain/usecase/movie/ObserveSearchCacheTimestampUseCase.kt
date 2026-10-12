/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.movie

import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSearchCacheTimestampUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(
        query: String,
        language: String,
        includeAdult: Boolean = false,
    ): Flow<Long?> {
        return repository.observeSearchCacheTimestamp(
            query = query,
            language = language,
            includeAdult = includeAdult
        )
    }
}
