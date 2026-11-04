/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans.notification

internal object PlanNotificationContract {
    const val RELEASE_CHANNEL_ID = "tracked_releases"
    const val MOVIE_NIGHT_CHANNEL_ID = "movie_nights"
    const val JOURNEY_CHANNEL_ID = "movie_night_progress"

    const val RELEASE_NOTIFICATION_OFFSET = 30_000
    const val MOVIE_NIGHT_REMINDER_OFFSET = 40_000
    const val MOVIE_NIGHT_JOURNEY_OFFSET = 45_000
    const val MOVIE_REQUEST_OFFSET = 50_000
    const val RELEASES_REQUEST_CODE = 60_001
    const val MOVIE_NIGHTS_REQUEST_CODE = 60_002
    const val PROGRESS_REQUEST_OFFSET = 70_000

    const val RELEASES_TAB = "releases"
    const val MOVIE_NIGHTS_TAB = "movie_nights"
    const val PROGRESS_MAX = 100
}
