/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.EntryPointAccessors

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
