/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.person.usecase

import com.norbertotaveras.reels.domain.person.model.FilmographySort
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditDataModel
import javax.inject.Inject

class SortPersonMovieCreditsUseCase @Inject constructor() {
    operator fun invoke(
        credits: List<PersonMovieCreditDataModel>,
        sort: FilmographySort,
    ): List<PersonMovieCreditDataModel> {
        return when (sort) {
            FilmographySort.Popular -> credits.sortedByDescending { movie -> movie.popularity }
            FilmographySort.Newest -> credits.sortedWith(
                compareByDescending<PersonMovieCreditDataModel> { movie ->
                    movie.releaseDate.releaseYearOrZero()
                }.thenByDescending { movie -> movie.popularity }
            )
            FilmographySort.HighestRated -> credits.sortedWith(
                compareByDescending<PersonMovieCreditDataModel> { movie -> movie.voteAverage }
                    .thenByDescending { movie -> movie.popularity }
            )
        }
    }
}

private fun String?.releaseYearOrZero(): Int {
    return this?.take(YEAR_LENGTH)?.toIntOrNull() ?: 0
}

private const val YEAR_LENGTH = 4
