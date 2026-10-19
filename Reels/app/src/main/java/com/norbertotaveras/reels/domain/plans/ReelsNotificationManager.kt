/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans

import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease

interface ReelsNotificationManager {
    fun createChannels()
    fun canPostNotifications(): Boolean
    fun showReleaseReminder(release: TrackedRelease)
    fun showMovieNightReminder(plan: MovieNightPlan)
    fun showMovieNightCountdown(plan: MovieNightPlan)
    fun showMovieNightProgress(plan: MovieNightPlan)
    fun showMovieNightCompleted(plan: MovieNightPlan)
    fun cancelMovieNightProgress(planId: String)
}
