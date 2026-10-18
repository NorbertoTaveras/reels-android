/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.plans

import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.repository.plans.MovieNightRepository
import javax.inject.Inject

class UpdateMovieNightUseCase @Inject constructor(
    private val repository: MovieNightRepository,
) {
    suspend fun edit(plan: MovieNightPlan) = repository.edit(plan)
    suspend fun start(id: String) = repository.start(id)
    suspend fun markStartingSoon(id: String) = repository.markStartingSoon(id)
    suspend fun markMissed(id: String) = repository.markMissed(id)
    suspend fun pause(id: String) = repository.pause(id)
    suspend fun resume(id: String) = repository.resume(id)
    suspend fun finish(id: String) = repository.finish(id)
    suspend fun cancel(id: String) = repository.cancel(id)
    suspend fun remove(id: String) = repository.remove(id)
}
