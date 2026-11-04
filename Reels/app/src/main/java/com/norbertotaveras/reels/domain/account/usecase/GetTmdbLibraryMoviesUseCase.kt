/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.account.usecase

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.account.model.TmdbLibraryList
import com.norbertotaveras.reels.domain.account.repository.TmdbAccountRepository
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTmdbLibraryMoviesUseCase @Inject constructor(
    private val repository: TmdbAccountRepository,
) {
    operator fun invoke(
        list: TmdbLibraryList,
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return when (list) {
            TmdbLibraryList.Watchlist -> repository.getWatchlistMovies(language = language)
            TmdbLibraryList.Rated -> repository.getRatedMovies(language = language)
        }
    }
}
