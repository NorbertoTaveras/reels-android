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
import com.norbertotaveras.reels.data.plans.scheduling.PlanAlarmContract
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import com.norbertotaveras.reels.domain.plans.repository.MovieNightRepository
import com.norbertotaveras.reels.domain.plans.repository.TrackedReleaseRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlanAlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var releases: TrackedReleaseRepository
    @Inject lateinit var movieNights: MovieNightRepository
    @Inject lateinit var notifications: ReelsNotificationManager
    @Inject lateinit var widgetUpdater: ReelsWidgetUpdater
    @Inject @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + ioDispatcher).launch {
            try {
                when (intent.action) {
                    PlanAlarmContract.ActionReleaseReminder -> showRelease(intent)
                    PlanAlarmContract.ActionMovieNightReminder -> showMovieNight(intent)
                    PlanAlarmContract.ActionMovieNightCountdown -> startMovieNightCountdown(intent)
                    PlanAlarmContract.ActionMovieNightMissed -> markMovieNightMissed(intent)
                }
                widgetUpdater.updateAll()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun showRelease(intent: Intent) {
        val movieId = intent.getIntExtra(PlanAlarmContract.ExtraMovieId, -1)
        releases.getTrackedReleases().firstOrNull { it.movieId == movieId }?.let {
            notifications.showReleaseReminder(it)
        }
    }

    private suspend fun showMovieNight(intent: Intent) {
        val planId = intent.getStringExtra(PlanAlarmContract.ExtraPlanId) ?: return
        movieNights.getMovieNights().firstOrNull { it.id == planId }?.let {
            notifications.showMovieNightReminder(it)
        }
    }

    private suspend fun startMovieNightCountdown(intent: Intent) {
        val planId = intent.getStringExtra(PlanAlarmContract.ExtraPlanId) ?: return
        if (movieNights.markStartingSoon(planId).isFailure) return
        movieNights.getMovieNights().firstOrNull { it.id == planId }?.let {
            notifications.showMovieNightCountdown(it)
        }
    }

    private suspend fun markMovieNightMissed(intent: Intent) {
        val planId = intent.getStringExtra(PlanAlarmContract.ExtraPlanId) ?: return
        if (movieNights.markMissed(planId).isSuccess) {
            notifications.cancelMovieNightProgress(planId)
        }
    }
}
