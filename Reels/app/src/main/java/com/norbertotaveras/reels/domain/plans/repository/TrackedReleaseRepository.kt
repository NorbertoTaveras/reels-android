/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans.repository

import com.norbertotaveras.reels.domain.plans.model.TrackedRelease
import kotlinx.coroutines.flow.Flow

interface TrackedReleaseRepository {
    fun observeTrackedReleases(): Flow<List<TrackedRelease>>
    fun observeTrackedRelease(movieId: Int): Flow<TrackedRelease?>
    suspend fun getTrackedReleases(): List<TrackedRelease>
    suspend fun track(release: TrackedRelease): Result<Unit>
    suspend fun updateReminder(release: TrackedRelease): Result<Unit>
    suspend fun refreshReleaseDate(movieId: Int, releaseDate: String?): Result<Unit>
    suspend fun remove(movieId: Int): Result<Unit>
}
