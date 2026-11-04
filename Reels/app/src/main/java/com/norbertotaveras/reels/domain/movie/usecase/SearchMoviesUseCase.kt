/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.movie.usecase

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import com.norbertotaveras.reels.domain.movie.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(
        query: String,
        language: String,
        includeAdult: Boolean = false
    ): Flow<PagingData<MovieDataModel>> {
        return repository.searchMovies(
            query = query,
            language = language,
            includeAdult = includeAdult
        )
    }
}
