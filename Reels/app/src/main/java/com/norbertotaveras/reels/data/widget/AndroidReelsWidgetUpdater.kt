/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import com.norbertotaveras.reels.presentation.widget.NextMovieNightWidget
import com.norbertotaveras.reels.presentation.widget.UpcomingReleasesWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidReelsWidgetUpdater @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ReelsWidgetUpdater {
    override suspend fun updateAll() {
        refresh(UpcomingReleasesWidget())
        refresh(NextMovieNightWidget())
    }

    private suspend fun refresh(widget: GlanceAppWidget) {
        GlanceAppWidgetManager(context).getGlanceIds(widget.javaClass).forEach { id ->
            // Invalidate time-derived labels even when Room has no new values to emit.
            updateAppWidgetState(context, id) { preferences ->
                preferences[RefreshRevision] = (preferences[RefreshRevision] ?: 0L) + 1L
            }
            widget.update(context, id)
        }
    }

    private companion object {
        val RefreshRevision = longPreferencesKey("plans_refresh_revision")
    }
}
