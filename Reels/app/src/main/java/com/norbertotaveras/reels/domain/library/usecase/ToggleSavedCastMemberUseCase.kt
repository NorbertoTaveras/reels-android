/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.library.usecase

import com.norbertotaveras.reels.domain.library.repository.SavedPeopleRepository
import com.norbertotaveras.reels.domain.person.repository.PersonRepository
import javax.inject.Inject

class ToggleSavedCastMemberUseCase @Inject constructor(
    private val personRepository: PersonRepository,
    private val savedPeopleRepository: SavedPeopleRepository,
) {
    suspend operator fun invoke(
        personId: Int,
        isSaved: Boolean,
        language: String,
    ): Result<Boolean> {
        if (isSaved) {
            return savedPeopleRepository.removeSavedPerson(personId = personId)
                .map { false }
        }

        val person = personRepository.getPersonDetails(
            personId = personId,
            language = language
        ).getOrElse { error ->
            return Result.failure(error)
        }
        val knownFor = personRepository.getPersonMovieCredits(
            personId = personId,
            language = language
        ).getOrNull()?.cast.orEmpty()

        return savedPeopleRepository.savePerson(
            person = person,
            knownFor = knownFor
        ).map { true }
    }
}
