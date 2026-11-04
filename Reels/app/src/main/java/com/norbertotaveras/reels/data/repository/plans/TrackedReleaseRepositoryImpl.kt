/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.plans

import com.norbertotaveras.reels.data.local.plans.dao.TrackedReleaseDao
import com.norbertotaveras.reels.data.local.plans.mapper.toDataModel
import com.norbertotaveras.reels.data.local.plans.mapper.toEntity
import com.norbertotaveras.reels.data.repository.repositoryResult
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.movie.extensions.toConfirmedReleaseDate
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease
import com.norbertotaveras.reels.domain.plans.repository.TrackedReleaseRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TrackedReleaseRepositoryImpl @Inject constructor(
    private val dao: TrackedReleaseDao,
    private val scheduler: PlanScheduler,
    private val widgetUpdater: ReelsWidgetUpdater,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TrackedReleaseRepository {
    override fun observeTrackedReleases(): Flow<List<TrackedRelease>> =
        dao.observeAll().map { releases -> releases.map { it.toDataModel() } }.flowOn(ioDispatcher)

    override fun observeTrackedRelease(movieId: Int): Flow<TrackedRelease?> =
        dao.observe(movieId).map { it?.toDataModel() }.flowOn(ioDispatcher)

    override suspend fun getTrackedReleases(): List<TrackedRelease> = withContext(ioDispatcher) {
        dao.getAll().map { it.toDataModel() }
    }

    override suspend fun track(release: TrackedRelease): Result<Unit> = repositoryResult(ioDispatcher) {
        val value = release.copy(
            releaseDate = release.releaseDate.toConfirmedReleaseDate(),
            previousReleaseDate = release.previousReleaseDate.toConfirmedReleaseDate(),
            updatedAt = System.currentTimeMillis(),
        )
        dao.upsert(value.toEntity())
        scheduler.scheduleRelease(value)
        widgetUpdater.updateAll()
    }

    override suspend fun updateReminder(release: TrackedRelease): Result<Unit> = track(release)

    override suspend fun refreshReleaseDate(movieId: Int, releaseDate: String?): Result<Unit> =
        repositoryResult(ioDispatcher) {
            val current = dao.get(movieId) ?: return@repositoryResult
            val confirmedDate = releaseDate.toConfirmedReleaseDate()
            if (current.releaseDate == confirmedDate) {
                widgetUpdater.updateAll()
                return@repositoryResult
            }
            val updated = current.copy(
                previousReleaseDate = current.releaseDate.toConfirmedReleaseDate(),
                releaseDate = confirmedDate,
                updatedAt = System.currentTimeMillis()
            ).toDataModel()
            dao.upsert(updated.toEntity())
            scheduler.scheduleRelease(updated)
            widgetUpdater.updateAll()
        }

    override suspend fun remove(movieId: Int): Result<Unit> = repositoryResult(ioDispatcher) {
        dao.delete(movieId)
        scheduler.cancelRelease(movieId)
        widgetUpdater.updateAll()
    }
}
