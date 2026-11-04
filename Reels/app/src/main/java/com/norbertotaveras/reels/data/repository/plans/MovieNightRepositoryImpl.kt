/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.plans

import com.norbertotaveras.reels.data.local.plans.dao.MovieNightDao
import com.norbertotaveras.reels.data.local.plans.mapper.toDataModel
import com.norbertotaveras.reels.data.local.plans.mapper.toEntity
import com.norbertotaveras.reels.data.repository.repositoryResult
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.MovieNightStatus
import com.norbertotaveras.reels.domain.plans.repository.MovieNightRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MovieNightRepositoryImpl @Inject constructor(
    private val dao: MovieNightDao,
    private val scheduler: PlanScheduler,
    private val widgetUpdater: ReelsWidgetUpdater,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MovieNightRepository {
    override fun observeMovieNights(): Flow<List<MovieNightPlan>> =
        dao.observeAll().map { plans -> plans.map { it.toDataModel() } }.flowOn(ioDispatcher)

    override fun observeMovieNight(id: String): Flow<MovieNightPlan?> =
        dao.observe(id).map { it?.toDataModel() }.flowOn(ioDispatcher)

    override fun observeUpcomingMovieNight(movieId: Int): Flow<MovieNightPlan?> =
        dao.observeUpcomingForMovie(movieId).map { it?.toDataModel() }.flowOn(ioDispatcher)

    override suspend fun getMovieNights(): List<MovieNightPlan> = withContext(ioDispatcher) {
        dao.getAll().map { it.toDataModel() }
    }

    override suspend fun schedule(plan: MovieNightPlan): Result<Unit> = repositoryResult(ioDispatcher) {
        val value = plan.copy(updatedAt = System.currentTimeMillis())
        dao.upsert(value.toEntity())
        scheduler.scheduleMovieNight(value)
        widgetUpdater.updateAll()
    }

    override suspend fun edit(plan: MovieNightPlan): Result<Unit> = schedule(plan)

    override suspend fun markStartingSoon(id: String): Result<Unit> = update(id) { current, now ->
        if (current.status == MovieNightStatus.Upcoming) {
            current.copy(status = MovieNightStatus.StartingSoon, updatedAt = now)
        } else {
            current
        }
    }

    override suspend fun markMissed(id: String): Result<Unit> = update(id) { current, now ->
        if (current.status == MovieNightStatus.Upcoming || current.status == MovieNightStatus.StartingSoon) {
            current.copy(status = MovieNightStatus.Missed, updatedAt = now)
        } else {
            current
        }
    }

    override suspend fun start(id: String): Result<Unit> = update(id) { current, now ->
        require(current.status == MovieNightStatus.Upcoming || current.status == MovieNightStatus.StartingSoon) {
            "Only an upcoming Movie Night can be started."
        }
        current.copy(
            status = MovieNightStatus.Active,
            startedAt = now,
            pausedAt = null,
            accumulatedPausedMillis = 0L,
            completedAt = null,
            updatedAt = now,
        )
    }

    override suspend fun pause(id: String): Result<Unit> = update(id) { current, now ->
        require(current.status == MovieNightStatus.Active) { "Only an active Movie Night can be paused." }
        current.copy(status = MovieNightStatus.Paused, pausedAt = now, updatedAt = now)
    }

    override suspend fun resume(id: String): Result<Unit> = update(id) { current, now ->
        require(current.status == MovieNightStatus.Paused) { "Only a paused Movie Night can be resumed." }
        val pausedDuration = current.pausedAt?.let { now - it } ?: 0L
        current.copy(
            status = MovieNightStatus.Active,
            pausedAt = null,
            accumulatedPausedMillis = current.accumulatedPausedMillis + pausedDuration,
            updatedAt = now
        )
    }

    override suspend fun finish(id: String): Result<Unit> = update(id) { current, now ->
        require(current.status == MovieNightStatus.Active || current.status == MovieNightStatus.Paused) {
            "Only a started Movie Night can be finished."
        }
        val pausedDuration = if (current.status == MovieNightStatus.Paused) {
            current.pausedAt?.let { now - it } ?: 0L
        } else {
            0L
        }
        current.copy(status = MovieNightStatus.Completed, completedAt = now, updatedAt = now)
            .copy(
                pausedAt = null,
                accumulatedPausedMillis = current.accumulatedPausedMillis + pausedDuration,
            )
    }

    override suspend fun cancel(id: String): Result<Unit> = update(id) { current, now ->
        require(current.status != MovieNightStatus.Completed && current.status != MovieNightStatus.Cancelled) {
            "This Movie Night has already ended."
        }
        current.copy(status = MovieNightStatus.Cancelled, updatedAt = now)
    }

    override suspend fun remove(id: String): Result<Unit> = repositoryResult(ioDispatcher) {
        dao.delete(id)
        scheduler.cancelMovieNight(id)
        widgetUpdater.updateAll()
    }

    private suspend fun update(
        id: String,
        transform: (MovieNightPlan, Long) -> MovieNightPlan,
    ): Result<Unit> = repositoryResult(ioDispatcher) {
        val current = dao.get(id)?.toDataModel() ?: error("Movie Night plan was not found.")
        val updated = transform(current, System.currentTimeMillis())
        dao.upsert(updated.toEntity())
        if (updated.status == MovieNightStatus.Upcoming || updated.status == MovieNightStatus.StartingSoon) {
            scheduler.scheduleMovieNight(updated)
        } else {
            scheduler.cancelMovieNight(id)
        }
        widgetUpdater.updateAll()
    }
}
