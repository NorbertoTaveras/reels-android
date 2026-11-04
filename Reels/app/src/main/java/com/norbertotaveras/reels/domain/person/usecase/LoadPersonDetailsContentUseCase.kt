/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.person.usecase

import com.norbertotaveras.reels.domain.library.mapper.toPersonDetailsDataModel
import com.norbertotaveras.reels.domain.library.repository.SavedPeopleRepository
import com.norbertotaveras.reels.domain.person.model.PersonDetailsContentDataModel
import com.norbertotaveras.reels.domain.person.repository.PersonRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class LoadPersonDetailsContentUseCase @Inject constructor(
    private val personRepository: PersonRepository,
    private val savedPeopleRepository: SavedPeopleRepository,
) {
    suspend operator fun invoke(
        personId: Int,
        language: String,
    ): Result<PersonDetailsContentDataModel> = coroutineScope {
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
        val creditsResult = creditsDeferred.await()
        val person = personResult.getOrNull()

        if (person != null) {
            Result.success(
                PersonDetailsContentDataModel(
                    person = person,
                    knownFor = creditsResult.getOrNull()?.cast.orEmpty(),
                    isOfflineSavedPerson = false
                )
            )
        } else {
            val savedPerson = savedPeopleRepository.getSavedPerson(personId = personId)
            if (savedPerson != null) {
                Result.success(
                    PersonDetailsContentDataModel(
                        person = savedPerson.toPersonDetailsDataModel(),
                        knownFor = savedPerson.knownFor,
                        isOfflineSavedPerson = true
                    )
                )
            } else {
                Result.failure(
                    personResult.exceptionOrNull()
                        ?: IllegalStateException("Person details request failed")
                )
            }
        }
    }
}
