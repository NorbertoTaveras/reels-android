/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans.usecase

import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.MovieNightStatus
import com.norbertotaveras.reels.domain.plans.repository.MovieNightRepository
import javax.inject.Inject

class UpdateMovieNightUseCase @Inject constructor(
    private val repository: MovieNightRepository,
) {
    suspend fun edit(plan: MovieNightPlan): Result<Unit> {
        if (plan.status != MovieNightStatus.Upcoming &&
            plan.status != MovieNightStatus.StartingSoon &&
            plan.status != MovieNightStatus.Missed
        ) {
            return Result.failure(
                IllegalArgumentException("Only an upcoming or missed Movie Night can be rescheduled.")
            )
        }
        if (plan.scheduledAt <= System.currentTimeMillis()) {
            return Result.failure(IllegalArgumentException("Choose a future date and time."))
        }
        return repository.edit(plan.copy(status = MovieNightStatus.Upcoming))
    }

    suspend fun start(id: String) = repository.start(id)
    suspend fun pause(id: String) = repository.pause(id)
    suspend fun resume(id: String) = repository.resume(id)
    suspend fun finish(id: String) = repository.finish(id)
    suspend fun cancel(id: String) = repository.cancel(id)
    suspend fun remove(id: String) = repository.remove(id)
}
