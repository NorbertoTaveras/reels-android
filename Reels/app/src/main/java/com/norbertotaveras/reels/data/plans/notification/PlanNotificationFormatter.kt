/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.plans.notification

import android.content.Context
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

internal object PlanNotificationFormatter {
    fun releaseContent(context: Context, release: TrackedRelease): String {
        val date = release.releaseDate?.let { value ->
            runCatching { LocalDate.parse(value).format(releaseDateFormatter) }.getOrDefault(value)
        }
        return date?.let { context.getString(R.string.release_notification_date, it) }
            ?: context.getString(R.string.release_notification_date_available)
    }

    fun movieNightTime(plan: MovieNightPlan): String = Instant.ofEpochMilli(plan.scheduledAt)
        .atZone(ZoneId.systemDefault())
        .format(movieNightTimeFormatter)

    fun countdownContent(
        context: Context,
        plan: MovieNightPlan,
        now: Instant = Instant.now(),
    ): String {
        val remainingMinutes = ChronoUnit.MINUTES.between(
            now,
            Instant.ofEpochMilli(plan.scheduledAt),
        ).coerceAtLeast(0L)
        return if (remainingMinutes == 0L) {
            context.getString(R.string.movie_night_countdown_now)
        } else {
            context.getString(R.string.movie_night_countdown_minutes, remainingMinutes)
        }
    }

    private val releaseDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    private val movieNightTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
}
