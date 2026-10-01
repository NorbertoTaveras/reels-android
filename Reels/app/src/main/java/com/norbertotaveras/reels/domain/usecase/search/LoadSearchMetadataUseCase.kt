/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.search

import com.norbertotaveras.reels.domain.model.search.SearchMetadataDataModel
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieGenresUseCase
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

class LoadSearchMetadataUseCase @Inject constructor(
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getTrendingSearchesUseCase: GetTrendingSearchesUseCase,
) {
    suspend operator fun invoke(language: String): SearchMetadataDataModel = supervisorScope {
        val genres = async {
            getMovieGenresUseCase(language = language).getOrDefault(emptyList())
        }
        val trendingSearches = async {
            getTrendingSearchesUseCase(language = language).getOrDefault(emptyList())
        }

        SearchMetadataDataModel(
            genres = genres.await(),
            trendingSearches = trendingSearches.await()
        )
    }
}
