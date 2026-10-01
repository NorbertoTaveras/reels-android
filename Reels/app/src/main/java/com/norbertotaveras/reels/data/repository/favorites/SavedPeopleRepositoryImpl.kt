/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.favorites

import com.norbertotaveras.reels.data.local.dao.SavedPersonDao
import com.norbertotaveras.reels.data.local.mapper.toDataModel
import com.norbertotaveras.reels.data.local.mapper.toSavedPersonEntity
import com.norbertotaveras.reels.data.local.mapper.toSavedPersonKnownForEntity
import com.norbertotaveras.reels.domain.model.favorite.SavedPersonDataModel
import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import com.norbertotaveras.reels.domain.repository.favorites.SavedPeopleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedPeopleRepositoryImpl @Inject constructor(
    private val savedPersonDao: SavedPersonDao
) : SavedPeopleRepository {
    override fun observeSavedPeople(): Flow<List<SavedPersonDataModel>> {
        return savedPersonDao.observeSavedPeople()
            .map { people -> people.map { person -> person.toDataModel() } }
    }

    override fun observeIsPersonSaved(personId: Int): Flow<Boolean> {
        return savedPersonDao.observeIsPersonSaved(personId = personId)
    }

    override suspend fun getSavedPerson(personId: Int): SavedPersonDataModel? {
        return savedPersonDao.getSavedPerson(personId = personId)?.toDataModel()
    }

    override suspend fun savePerson(
        person: PersonDetailsDataModel,
        knownFor: List<PersonMovieCreditDataModel>
    ): Result<Unit> {
        return runCatching {
            val savedAt = System.currentTimeMillis()
            savedPersonDao.upsertSavedPersonWithKnownFor(
                person = person.toSavedPersonEntity(savedAt = savedAt),
                knownFor = knownFor.mapIndexed { index, movie ->
                    movie.toSavedPersonKnownForEntity(
                        personId = person.id,
                        position = index
                    )
                }
            )
        }
    }

    override suspend fun removeSavedPerson(personId: Int): Result<Unit> {
        return runCatching {
            savedPersonDao.deleteSavedPerson(personId = personId)
        }
    }
}
