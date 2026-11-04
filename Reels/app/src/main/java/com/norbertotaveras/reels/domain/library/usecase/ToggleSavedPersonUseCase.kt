/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.library.usecase

import com.norbertotaveras.reels.domain.library.repository.SavedPeopleRepository
import com.norbertotaveras.reels.domain.person.model.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditDataModel
import javax.inject.Inject

class ToggleSavedPersonUseCase @Inject constructor(
    private val savedPeopleRepository: SavedPeopleRepository
) {
    suspend operator fun invoke(
        isSaved: Boolean,
        person: PersonDetailsDataModel,
        knownFor: List<PersonMovieCreditDataModel>
    ): Result<Boolean> {
        return if (isSaved) {
            savedPeopleRepository.removeSavedPerson(personId = person.id)
                .map { false }
        } else {
            savedPeopleRepository.savePerson(
                person = person,
                knownFor = knownFor
            ).map { true }
        }
    }
}
