/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans

import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease

interface PlanScheduler {
    fun canScheduleExactAlarms(): Boolean
    fun scheduleRelease(release: TrackedRelease)
    fun cancelRelease(movieId: Int)
    fun scheduleMovieNight(plan: MovieNightPlan)
    fun cancelMovieNight(id: String)
}
