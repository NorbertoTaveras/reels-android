/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.account

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.repository.account.TmdbAccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTmdbWatchlistMoviesUseCase @Inject constructor(
    private val repository: TmdbAccountRepository,
) {
    operator fun invoke(
        language: String = "en-US",
    ): Flow<PagingData<MovieDataModel>> {
        return repository.getWatchlistMovies(language = language)
    }
}
