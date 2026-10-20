/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.plans

import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.domain.repository.plans.TrackedReleaseRepository
import javax.inject.Inject

class TrackReleaseUseCase @Inject constructor(
    private val repository: TrackedReleaseRepository,
) {
    suspend operator fun invoke(release: TrackedRelease): Result<Unit> = repository.track(release)
    suspend fun updateReminder(release: TrackedRelease): Result<Unit> = repository.updateReminder(release)
    suspend fun remove(movieId: Int): Result<Unit> = repository.remove(movieId)
}
