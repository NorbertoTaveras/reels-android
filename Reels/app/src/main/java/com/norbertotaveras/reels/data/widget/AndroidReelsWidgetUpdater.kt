/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.norbertotaveras.reels.domain.plans.ReelsWidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidReelsWidgetUpdater @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ReelsWidgetUpdater {
    override suspend fun updateAll() {
        UpcomingReleasesWidget().updateAll(context)
        NextMovieNightWidget().updateAll(context)
    }
}
