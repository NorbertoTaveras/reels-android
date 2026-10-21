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
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import com.norbertotaveras.reels.domain.plans.hasRuntimeEnded
import com.norbertotaveras.reels.domain.repository.plans.MovieNightRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieNightActionReceiver : BroadcastReceiver() {
    @Inject lateinit var movieNights: MovieNightRepository
    @Inject lateinit var notifications: ReelsNotificationManager
    @Inject lateinit var widgetUpdater: ReelsWidgetUpdater
    @Inject @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher

    override fun onReceive(context: Context, intent: Intent) {
        val planId = intent.getStringExtra(PlanAlarmContract.ExtraPlanId) ?: return
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + ioDispatcher).launch {
            try {
                val mutationResult = when (intent.action) {
                    PlanAlarmContract.ActionMovieNightStart -> movieNights.start(planId)
                    PlanAlarmContract.ActionMovieNightPause -> movieNights.pause(planId)
                    PlanAlarmContract.ActionMovieNightResume -> movieNights.resume(planId)
                    PlanAlarmContract.ActionMovieNightFinish -> movieNights.finish(planId)
                    PlanAlarmContract.ActionMovieNightEnd -> movieNights.cancel(planId)
                    PlanAlarmContract.ActionMovieNightProgress -> Result.success(Unit)
                    else -> Result.failure(IllegalArgumentException("Unsupported Movie Night action."))
                }
                if (mutationResult.isFailure) return@launch
                val plan = movieNights.getMovieNights().firstOrNull { it.id == planId } ?: return@launch
                when (intent.action) {
                    PlanAlarmContract.ActionMovieNightFinish -> notifications.showMovieNightCompleted(plan)
                    PlanAlarmContract.ActionMovieNightEnd -> notifications.cancelMovieNightProgress(planId)
                    PlanAlarmContract.ActionMovieNightProgress -> {
                        updateProgress(plan)
                        widgetUpdater.updateAll()
                    }
                    else -> notifications.showMovieNightProgress(plan)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun updateProgress(plan: MovieNightPlan) {
        when (plan.status) {
            MovieNightStatus.StartingSoon ->
                notifications.showMovieNightCountdown(plan)

            MovieNightStatus.Active -> {
                if (plan.hasRuntimeEnded()) {
                    movieNights.finish(plan.id)
                    val completed = movieNights.getMovieNights().firstOrNull { it.id == plan.id } ?: plan
                    notifications.showMovieNightCompleted(completed)
                } else {
                    notifications.showMovieNightProgress(plan)
                }
            }

            else -> notifications.cancelMovieNightProgress(plan.id)
        }
    }
}
