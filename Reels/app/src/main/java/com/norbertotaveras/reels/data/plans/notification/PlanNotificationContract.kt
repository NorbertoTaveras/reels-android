/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans.notification

internal object PlanNotificationContract {
    const val ReleaseChannelId = "tracked_releases"
    const val MovieNightChannelId = "movie_nights"
    const val JourneyChannelId = "movie_night_progress"

    const val ReleaseNotificationOffset = 30_000
    const val MovieNightReminderOffset = 40_000
    const val MovieNightJourneyOffset = 45_000
    const val MovieRequestOffset = 50_000
    const val ReleasesRequestCode = 60_001
    const val MovieNightsRequestCode = 60_002
    const val ProgressRequestOffset = 70_000

    const val ReleasesTab = "releases"
    const val MovieNightsTab = "movie_nights"
    const val ProgressMax = 100
}
