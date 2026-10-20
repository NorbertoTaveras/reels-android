/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val fullDateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM d")
private val movieNightDateFormatter = DateTimeFormatter.ofPattern("MMM d")
private val movieNightTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

internal fun TrackedRelease.releaseDateLabel(today: LocalDate = LocalDate.now()): String {
    val date = releaseDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return "TBA"
    val days = ChronoUnit.DAYS.between(today, date)
    val relative = when {
        days == 0L -> "today"
        days == 1L -> "1 day"
        days in 2..13 -> "$days days"
        days in 14..59 -> "${days / 7}w"
        days >= 60 -> "${days / 30}mo"
        else -> null
    }
    return buildString {
        append(date.format(fullDateFormatter))
        if (relative != null) append(" · $relative")
    }
}

internal fun TrackedRelease.releaseStatusLabel(today: LocalDate = LocalDate.now()): String? {
    val date = releaseDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    return when {
        previousReleaseDate != null && previousReleaseDate != releaseDate -> "Date moved"
        date == today -> "Out today"
        date != null && date.isBefore(today) -> "Released"
        else -> null
    }
}

internal fun TrackedRelease.previousDateLabel(): String? = previousReleaseDate
    ?.takeIf { it != releaseDate }
    ?.let { value -> runCatching { LocalDate.parse(value).format(shortDateFormatter) }.getOrNull() }

internal fun TrackedRelease.reminderLabel(): String = when {
    !notificationsEnabled -> "Notifications off"
    releaseDate == null -> "Waiting for a confirmed date"
    reminder == ReleaseReminder.OnReleaseDay -> "On release day"
    reminder == ReleaseReminder.OneWeekBefore -> "1 week before"
    reminder == ReleaseReminder.Custom -> "Custom reminder"
    reminder == ReleaseReminder.None -> "No reminder"
    else -> "1 day before"
}

internal fun MovieNightPlan.dateLabel(): String = Instant.ofEpochMilli(scheduledAt)
    .atZone(ZoneId.systemDefault())
    .format(movieNightDateFormatter)

internal fun MovieNightPlan.timeLabel(): String = Instant.ofEpochMilli(scheduledAt)
    .atZone(ZoneId.systemDefault())
    .format(movieNightTimeFormatter)

internal fun MovieNightPlan.reminderLabel(): String = reminderMinutesBefore?.let { minutes ->
    when {
        minutes >= 1_440 -> "${minutes / 1_440} day before"
        minutes >= 60 -> "${minutes / 60} hr before"
        else -> "$minutes min before"
    }
} ?: "No reminder"

internal fun MovieNightPlan.scheduleConfirmationDetails(): String = buildString {
    append("${dateLabel()} · ${timeLabel()}")
    append('\n')
    if (reminderMinutesBefore != null) append("Reminder ")
    append(reminderLabel())
}

internal fun MovieNightPlan.statusLabel(): String = when (status) {
    MovieNightStatus.Upcoming -> "Upcoming"
    MovieNightStatus.StartingSoon -> "Starting soon"
    MovieNightStatus.Active -> "Watching now"
    MovieNightStatus.Paused -> "Paused"
    MovieNightStatus.Completed -> "Completed"
    MovieNightStatus.Missed -> "Missed"
    MovieNightStatus.Cancelled -> "Cancelled"
}

internal fun MovieNightPlan.progressFraction(now: Long = System.currentTimeMillis()): Float {
    val start = startedAt ?: return 0f
    val end = pausedAt ?: now
    val elapsed = (end - start - accumulatedPausedMillis).coerceAtLeast(0L)
    return (elapsed.toFloat() / (runtimeMinutes * 60_000L)).coerceIn(0f, 1f)
}

internal fun MovieNightPlan.progressLabel(): String {
    val remainingMinutes = ((runtimeMinutes * (1f - progressFraction()))).toInt().coerceAtLeast(0)
    val prefix = if (status == MovieNightStatus.Paused) "Paused" else "Now watching"
    return "$prefix · ${remainingMinutes / 60}h ${remainingMinutes % 60}m remaining"
}
