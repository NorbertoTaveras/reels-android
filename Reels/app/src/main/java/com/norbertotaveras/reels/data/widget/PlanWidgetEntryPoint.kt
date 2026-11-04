/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.widget

import com.norbertotaveras.reels.domain.network.NetworkMonitor
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.plans.repository.MovieNightRepository
import com.norbertotaveras.reels.domain.plans.repository.TrackedReleaseRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlanWidgetEntryPoint {
    fun trackedReleaseRepository(): TrackedReleaseRepository
    fun movieNightRepository(): MovieNightRepository
    fun notificationManager(): ReelsNotificationManager
    fun networkMonitor(): NetworkMonitor
}
