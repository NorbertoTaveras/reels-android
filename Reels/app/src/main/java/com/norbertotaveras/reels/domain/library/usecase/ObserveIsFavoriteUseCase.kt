/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.library.usecase

import com.norbertotaveras.reels.domain.library.repository.FavoriteMoviesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveIsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    operator fun invoke(movieId: Int): Flow<Boolean> {
        return repository.observeIsFavorite(movieId = movieId)
    }
}
