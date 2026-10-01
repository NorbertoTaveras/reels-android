/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.search

import com.norbertotaveras.reels.domain.repository.movie.MovieRepository
import java.util.Locale
import javax.inject.Inject

class GetTrendingSearchesUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(language: String): Result<List<String>> {
        return repository.getTrendingMovies(language = language)
            .map { movies ->
                movies.asSequence()
                    .map { movie -> movie.title.trim() }
                    .filter { title -> title.isNotEmpty() }
                    .distinctBy { title -> title.lowercase(Locale.ROOT) }
                    .take(MAX_TRENDING_SEARCHES)
                    .toList()
            }
    }

    private companion object {
        const val MAX_TRENDING_SEARCHES = 5
    }
}
