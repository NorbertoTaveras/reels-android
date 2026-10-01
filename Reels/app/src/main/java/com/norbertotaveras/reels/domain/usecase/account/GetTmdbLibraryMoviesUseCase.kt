/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.account

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.repository.account.TmdbAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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
