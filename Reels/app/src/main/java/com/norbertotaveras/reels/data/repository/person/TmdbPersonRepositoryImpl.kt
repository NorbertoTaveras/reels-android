/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.person

import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.mapper.person.toPersonDetailsDataModel
import com.norbertotaveras.reels.data.remote.mapper.person.toPersonMovieCreditsDataModel
import com.norbertotaveras.reels.data.repository.tmdbResult
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditsDataModel
import com.norbertotaveras.reels.domain.repository.person.PersonRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Singleton
class TmdbPersonRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbRemoteDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PersonRepository {
    override suspend fun getPersonDetails(
        personId: Int,
        language: String,
    ): Result<PersonDetailsDataModel> {
        return tmdbResult(ioDispatcher) {
            remoteDataSource.getPersonDetails(
                personId = personId,
                language = language
            ).toPersonDetailsDataModel()
        }
    }

    override suspend fun getPersonMovieCredits(
        personId: Int,
        language: String,
    ): Result<PersonMovieCreditsDataModel> {
        return tmdbResult(ioDispatcher) {
            remoteDataSource.getPersonMovieCredits(
                personId = personId,
                language = language
            ).toPersonMovieCreditsDataModel()
        }
    }
}
