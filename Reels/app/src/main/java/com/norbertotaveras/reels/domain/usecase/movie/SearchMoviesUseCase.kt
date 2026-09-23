/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.movie

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(
        query: String,
        language: String = "en-US",
        includeAdult: Boolean = false
    ): Flow<PagingData<MovieDataModel>> {
        return repository.searchMovies(
            query = query,
            language = language,
            includeAdult = includeAdult
        )
    }
}
