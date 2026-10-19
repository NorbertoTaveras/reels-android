/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import android.content.Context
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

internal fun String?.toRelativeReleaseDate(context: Context): String {
    if (this == null) return context.getString(R.string.widget_release_tba)
    return runCatching {
        val releaseDate = LocalDate.parse(this)
        when (val days = ChronoUnit.DAYS.between(LocalDate.now(), releaseDate)) {
            0L -> context.getString(R.string.widget_release_today)
            1L -> context.getString(R.string.widget_release_tomorrow)
            in 2..30 -> context.getString(R.string.widget_release_in_days, days)
            else -> releaseDate.format(RELEASE_DATE_FORMAT)
        }
    }.getOrDefault(this)
}

internal fun MovieNightPlan.toWidgetDateTime(): String = Instant.ofEpochMilli(scheduledAt)
    .atZone(ZoneId.systemDefault())
    .format(MOVIE_NIGHT_DATE_FORMAT)

internal fun MovieNightPlan.runtimeLabel(context: Context): String =
    runtimeMinutes.takeIf { it in 1..600 }?.let { context.getString(R.string.widget_runtime_minutes, it) }
        ?: context.getString(R.string.widget_runtime_unavailable)

internal fun ReleaseReminder.toWidgetLabel(context: Context): String = when (this) {
    ReleaseReminder.AppDefault,
    ReleaseReminder.OneDayBefore,
    -> context.getString(R.string.widget_reminder_one_day)

    ReleaseReminder.OnReleaseDay -> context.getString(R.string.widget_reminder_release_day)
    ReleaseReminder.OneWeekBefore -> context.getString(R.string.widget_reminder_one_week)
    ReleaseReminder.Custom -> context.getString(R.string.widget_reminder_custom)
    ReleaseReminder.None -> context.getString(R.string.widget_reminder_none)
}

private val RELEASE_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d")
private val MOVIE_NIGHT_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, MMM d · h:mm a")
