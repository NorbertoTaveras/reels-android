/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans

internal object PlanAlarmContract {
    const val ActionReleaseReminder = "com.norbertotaveras.reels.RELEASE_REMINDER"
    const val ActionMovieNightReminder = "com.norbertotaveras.reels.MOVIE_NIGHT_REMINDER"
    const val ActionMovieNightMissed = "com.norbertotaveras.reels.MOVIE_NIGHT_MISSED"
    const val ActionMovieNightStart = "com.norbertotaveras.reels.MOVIE_NIGHT_START"
    const val ActionMovieNightPause = "com.norbertotaveras.reels.MOVIE_NIGHT_PAUSE"
    const val ActionMovieNightResume = "com.norbertotaveras.reels.MOVIE_NIGHT_RESUME"
    const val ActionMovieNightFinish = "com.norbertotaveras.reels.MOVIE_NIGHT_FINISH"
    const val ActionMovieNightEnd = "com.norbertotaveras.reels.MOVIE_NIGHT_END"
    const val ActionMovieNightProgress = "com.norbertotaveras.reels.MOVIE_NIGHT_PROGRESS"

    const val ExtraMovieId = "movie_id"
    const val ExtraPlanId = "plan_id"
}
