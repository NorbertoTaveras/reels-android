/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.person

import com.norbertotaveras.reels.domain.model.person.FilmographyContentDataModel
import com.norbertotaveras.reels.domain.repository.person.PersonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LoadFilmographyContentUseCase @Inject constructor(
    private val personRepository: PersonRepository,
) {
    suspend operator fun invoke(
        personId: Int,
        language: String,
    ): Result<FilmographyContentDataModel> = coroutineScope {
        val personDeferred = async {
            personRepository.getPersonDetails(
                personId = personId,
                language = language
            )
        }
        val creditsDeferred = async {
            personRepository.getPersonMovieCredits(
                personId = personId,
                language = language
            )
        }

        val personResult = personDeferred.await()
        creditsDeferred.await().map { credits ->
            FilmographyContentDataModel(
                personName = personResult.getOrNull()?.name,
                movies = credits.cast
            )
        }
    }
}
