/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.favorites

import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import com.norbertotaveras.reels.domain.repository.favorites.SavedPeopleRepository
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
