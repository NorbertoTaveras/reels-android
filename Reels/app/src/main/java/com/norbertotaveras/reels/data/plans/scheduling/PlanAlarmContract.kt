/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans.scheduling

internal object PlanAlarmContract {
    const val ACTION_RELEASE_REMINDER = "com.norbertotaveras.reels.RELEASE_REMINDER"
    const val ACTION_MOVIE_NIGHT_REMINDER = "com.norbertotaveras.reels.MOVIE_NIGHT_REMINDER"
    const val ACTION_MOVIE_NIGHT_COUNTDOWN = "com.norbertotaveras.reels.MOVIE_NIGHT_COUNTDOWN"
    const val ACTION_MOVIE_NIGHT_MISSED = "com.norbertotaveras.reels.MOVIE_NIGHT_MISSED"
    const val ACTION_MOVIE_NIGHT_START = "com.norbertotaveras.reels.MOVIE_NIGHT_START"
    const val ACTION_MOVIE_NIGHT_PAUSE = "com.norbertotaveras.reels.MOVIE_NIGHT_PAUSE"
    const val ACTION_MOVIE_NIGHT_RESUME = "com.norbertotaveras.reels.MOVIE_NIGHT_RESUME"
    const val ACTION_MOVIE_NIGHT_FINISH = "com.norbertotaveras.reels.MOVIE_NIGHT_FINISH"
    const val ACTION_MOVIE_NIGHT_END = "com.norbertotaveras.reels.MOVIE_NIGHT_END"
    const val ACTION_MOVIE_NIGHT_PROGRESS = "com.norbertotaveras.reels.MOVIE_NIGHT_PROGRESS"

    const val EXTRA_MOVIE_ID = "movie_id"
    const val EXTRA_PLAN_ID = "plan_id"
}
