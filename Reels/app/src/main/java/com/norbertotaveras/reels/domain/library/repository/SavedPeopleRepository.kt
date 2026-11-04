/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.library.repository

import com.norbertotaveras.reels.domain.library.model.SavedPersonDataModel
import com.norbertotaveras.reels.domain.person.model.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditDataModel
import kotlinx.coroutines.flow.Flow

interface SavedPeopleRepository {
    fun observeSavedPeople(): Flow<List<SavedPersonDataModel>>
    fun observeIsPersonSaved(personId: Int): Flow<Boolean>
    suspend fun getSavedPerson(personId: Int): SavedPersonDataModel?
    suspend fun savePerson(
        person: PersonDetailsDataModel,
        knownFor: List<PersonMovieCreditDataModel>
    ): Result<Unit>
    suspend fun removeSavedPerson(personId: Int): Result<Unit>
}
