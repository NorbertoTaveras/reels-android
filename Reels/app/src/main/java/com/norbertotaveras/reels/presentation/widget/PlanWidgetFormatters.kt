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
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.plans.canStartJourney
import com.norbertotaveras.reels.domain.plans.runtimeRemainingMillis
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.max

internal fun MovieNightPlan.toRelativeWidgetDateTime(context: Context): String {
    val scheduled = Instant.ofEpochMilli(scheduledAt).atZone(ZoneId.systemDefault())
    val time = scheduled.format(MOVIE_NIGHT_TIME_FORMAT)
    return when (scheduled.toLocalDate()) {
        LocalDate.now() -> context.getString(R.string.widget_tonight_time, time)
        LocalDate.now().plusDays(1) -> context.getString(R.string.widget_tomorrow_time, time)
        else -> scheduled.format(MOVIE_NIGHT_DATE_FORMAT)
    }
}

internal fun MovieNightPlan.toWidgetCountdown(context: Context, short: Boolean = false): String {
    if (hasWidgetJourney) return runtimeRemainingLabel(context)
    val remaining = Duration.ofMillis((scheduledAt - System.currentTimeMillis()).coerceAtLeast(0L))
    val days = remaining.toDays()
    val hours = remaining.toHours()
    val minutes = remaining.toMinutes()
    return when {
        remaining.isZero -> context.getString(R.string.widget_now)
        short && days >= 60 -> "${max(1L, days / 30)}mo"
        short && days > 0 -> "${days}d"
        short && hours > 0 -> "${hours}h"
        short -> "${minutes}m"
        days > 0 -> "In ${days}d ${hours % 24}h"
        hours > 0 -> "In ${hours}h ${minutes % 60}m"
        minutes > 0 -> "In ${minutes}m"
        else -> context.getString(R.string.widget_now)
    }
}

internal val MovieNightPlan.hasWidgetJourney: Boolean
    get() = status == MovieNightStatus.Active || status == MovieNightStatus.Paused

internal fun MovieNightPlan.canStartFromWidget(): Boolean =
    status == MovieNightStatus.StartingSoon ||
        (status == MovieNightStatus.Upcoming && canStartJourney())

internal fun MovieNightPlan.runtimeRemainingLabel(context: Context): String {
    val minutes = (runtimeRemainingMillis() + MILLIS_PER_MINUTE - 1) / MILLIS_PER_MINUTE
    return context.getString(R.string.widget_runtime_remaining, minutes / 60, minutes % 60)
}

internal fun String?.toWidgetReleaseDate(context: Context, isOnline: Boolean): String {
    if (this == null) return context.getString(R.string.widget_release_tba)
    return runCatching {
        val date = LocalDate.parse(this)
        val formattedDate = date.format(RELEASE_DATE_FORMAT)
        if (!isOnline) return@runCatching context.getString(R.string.widget_cached_date, formattedDate)
        val days = ChronoUnit.DAYS.between(LocalDate.now(), date)
        val relative = when (days) {
            0L -> context.getString(R.string.widget_release_today)
            1L -> context.getString(R.string.widget_release_tomorrow)
            in 2..30 -> context.getString(R.string.widget_release_days, days)
            else -> null
        }
        if (relative == null) formattedDate else "$formattedDate · $relative"
    }.getOrDefault(this)
}

internal fun String?.toCompactWidgetReleaseDate(context: Context, isOnline: Boolean): String {
    if (!isOnline) return context.getString(R.string.widget_cached)
    val date = this?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: return context.getString(R.string.widget_release_tba)
    return when (val days = ChronoUnit.DAYS.between(LocalDate.now(), date)) {
        0L -> context.getString(R.string.widget_release_today)
        1L -> context.getString(R.string.widget_release_tomorrow)
        in 2..Long.MAX_VALUE -> context.getString(R.string.widget_release_in_days, days)
        else -> date.format(RELEASE_DATE_FORMAT)
    }
}

internal fun String.toWidgetReleaseCountdown(): String {
    val days = runCatching { ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(this)) }
        .getOrDefault(0L)
        .coerceAtLeast(0L)
    return when {
        days >= 60 -> "${max(1L, days / 30)}mo"
        else -> "${days}d"
    }
}

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
private val MOVIE_NIGHT_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a")
private const val MILLIS_PER_MINUTE = 60_000L
