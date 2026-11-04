/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans.usecase

import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.repository.MovieNightRepository
import javax.inject.Inject

class ScheduleMovieNightUseCase @Inject constructor(
    private val repository: MovieNightRepository,
) {
    suspend operator fun invoke(plan: MovieNightPlan): Result<Unit> = repository.schedule(plan)
}
