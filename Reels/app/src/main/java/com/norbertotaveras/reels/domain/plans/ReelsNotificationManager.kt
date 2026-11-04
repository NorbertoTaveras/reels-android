/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans

import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease

interface ReelsNotificationManager {
    fun createChannels()
    fun canPostNotifications(): Boolean
    fun supportsLiveUpdates(): Boolean
    fun canPromoteLiveUpdates(): Boolean
    fun showReleaseReminder(release: TrackedRelease)
    fun showMovieNightReminder(plan: MovieNightPlan)
    suspend fun showMovieNightCountdown(plan: MovieNightPlan)
    suspend fun showMovieNightProgress(plan: MovieNightPlan)
    fun showMovieNightCompleted(plan: MovieNightPlan)
    fun cancelMovieNightProgress(planId: String)
}
