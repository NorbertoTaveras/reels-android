/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.updateAll
import com.norbertotaveras.reels.MainActivity
import com.norbertotaveras.reels.data.widget.PlanWidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors

internal fun plansAction(context: Context, tab: String) = actionStartActivity(
    Intent(Intent.ACTION_VIEW, "reels://plans?tab=$tab".toUri(), context, MainActivity::class.java),
)

internal fun movieAction(context: Context, movieId: Int) = actionStartActivity(
    Intent(Intent.ACTION_VIEW, "reels://movie/$movieId".toUri(), context, MainActivity::class.java),
)

class StartMovieNightAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val planId = parameters[PlanIdKey] ?: return
        val entryPoint = EntryPointAccessors.fromApplication(context, PlanWidgetEntryPoint::class.java)
        if (entryPoint.movieNightRepository().start(planId).isFailure) return
        val plan = entryPoint.movieNightRepository().getMovieNights().firstOrNull { it.id == planId }
        if (plan != null) entryPoint.notificationManager().showMovieNightProgress(plan)
        UpcomingReleasesWidget().updateAll(context)
        NextMovieNightWidget().updateAll(context)
    }

    companion object {
        val PlanIdKey = ActionParameters.Key<String>("plan_id")
    }
}
