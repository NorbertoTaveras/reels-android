/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.person

import androidx.room.withTransaction
import com.norbertotaveras.reels.data.local.cache.CachedContentType
import com.norbertotaveras.reels.data.local.cache.personDetailsCacheKey
import com.norbertotaveras.reels.data.local.dao.CachedContentDao
import com.norbertotaveras.reels.data.local.db.ReelsDatabase
import com.norbertotaveras.reels.data.local.entity.CachedContentStatusEntity
import com.norbertotaveras.reels.data.local.mapper.toCachedPersonDetailsEntity
import com.norbertotaveras.reels.data.local.mapper.toCachedPersonMovieCreditEntities
import com.norbertotaveras.reels.data.local.mapper.toPersonDetailsDataModel as toCachedPersonDetailsDataModel
import com.norbertotaveras.reels.data.local.mapper.toPersonMovieCreditsDataModel as toCachedPersonMovieCreditsDataModel
import com.norbertotaveras.reels.data.remote.mapper.person.toPersonDetailsDataModel
import com.norbertotaveras.reels.data.remote.mapper.person.toPersonMovieCreditsDataModel
import com.norbertotaveras.reels.data.remote.person.TmdbPersonRemoteDataSource
import com.norbertotaveras.reels.data.repository.cachedTmdbResult
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditsDataModel
import com.norbertotaveras.reels.domain.network.NetworkMonitor
import com.norbertotaveras.reels.domain.repository.person.PersonRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Singleton
class TmdbPersonRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbPersonRemoteDataSource,
    private val database: ReelsDatabase,
    private val cachedContentDao: CachedContentDao,
    private val networkMonitor: NetworkMonitor,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PersonRepository {
    override suspend fun getPersonDetails(
        personId: Int,
        language: String,
    ): Result<PersonDetailsDataModel> {
        val cacheKey = personDetailsCacheKey(personId = personId, language = language)
        return cachedTmdbResult(
            dispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            loadRemote = {
                remoteDataSource.getPersonDetails(
                    personId = personId,
                    language = language
                ).toPersonDetailsDataModel()
            },
            saveRemote = { person ->
                cachedContentDao.upsertPersonDetails(
                    person.toCachedPersonDetailsEntity(
                        cacheKey = cacheKey,
                        cachedAt = System.currentTimeMillis()
                    )
                )
            },
            loadCached = {
                cachedContentDao.getPersonDetails(cacheKey = cacheKey)
                    ?.toCachedPersonDetailsDataModel()
            }
        )
    }

    override suspend fun getPersonMovieCredits(
        personId: Int,
        language: String,
    ): Result<PersonMovieCreditsDataModel> {
        val cacheKey = personDetailsCacheKey(personId = personId, language = language)
        return cachedTmdbResult(
            dispatcher = ioDispatcher,
            networkMonitor = networkMonitor,
            loadRemote = {
                remoteDataSource.getPersonMovieCredits(
                    personId = personId,
                    language = language
                ).toPersonMovieCreditsDataModel()
            },
            saveRemote = { credits ->
                val now = System.currentTimeMillis()
                database.withTransaction {
                    cachedContentDao.clearPersonMovieCredits(cacheKey = cacheKey)
                    cachedContentDao.upsertPersonMovieCredits(
                        credits.toCachedPersonMovieCreditEntities(
                            cacheKey = cacheKey,
                            cachedAt = now
                        )
                    )
                    cachedContentDao.upsertContentStatus(
                        CachedContentStatusEntity(
                            cacheKey = cacheKey,
                            contentType = CachedContentType.PERSON_CREDITS,
                            cachedAt = now
                        )
                    )
                }
            },
            loadCached = {
                if (cachedContentDao.hasContent(cacheKey, CachedContentType.PERSON_CREDITS)) {
                    cachedContentDao.getPersonMovieCredits(cacheKey = cacheKey).toCachedPersonMovieCreditsDataModel()
                } else {
                    null
                }
            }
        )
    }
}
