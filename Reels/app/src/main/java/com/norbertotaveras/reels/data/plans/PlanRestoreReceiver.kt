/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import com.norbertotaveras.reels.domain.repository.plans.MovieNightRepository
import com.norbertotaveras.reels.domain.repository.plans.TrackedReleaseRepository
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlanRestoreReceiver : BroadcastReceiver() {
    @Inject lateinit var releases: TrackedReleaseRepository
    @Inject lateinit var movieNights: MovieNightRepository
    @Inject lateinit var scheduler: PlanScheduler
    @Inject lateinit var widgetUpdater: ReelsWidgetUpdater
    @Inject lateinit var notifications: ReelsNotificationManager
    @Inject @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + ioDispatcher).launch {
            try {
                releases.getTrackedReleases().forEach(scheduler::scheduleRelease)
                movieNights.getMovieNights().forEach { plan ->
                    when {
                        (plan.status == MovieNightStatus.Upcoming || plan.status == MovieNightStatus.StartingSoon) &&
                            plan.scheduledAt + MISSED_GRACE_MILLIS <= System.currentTimeMillis() -> {
                            movieNights.markMissed(plan.id)
                        }
                        plan.status == MovieNightStatus.Active || plan.status == MovieNightStatus.Paused -> {
                            notifications.showMovieNightProgress(plan)
                        }
                        else -> scheduler.scheduleMovieNight(plan)
                    }
                }
                widgetUpdater.updateAll()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        const val MISSED_GRACE_MILLIS = 15 * 60_000L
    }
}
