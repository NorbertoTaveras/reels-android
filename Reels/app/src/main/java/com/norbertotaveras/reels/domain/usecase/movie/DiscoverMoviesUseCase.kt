/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.movie

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DiscoverMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(
        filters: DiscoverFilterDataModel,
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return repository.discoverMovies(
            filters = filters,
            language = language
        )
    }
}
