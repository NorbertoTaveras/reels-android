/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList

internal fun Long?.toTmdbMovieActivityLabel(
    listType: TmdbLibraryList,
    nowMillis: Long = System.currentTimeMillis(),
): String {
    val action = when (listType) {
        TmdbLibraryList.Watchlist -> "Added"
        TmdbLibraryList.Rated -> "Rated"
    }
    val occurredAtMillis = this ?: return "$action on TMDB"
    val elapsedMillis = (nowMillis - occurredAtMillis).coerceAtLeast(0L)

    return "$action ${elapsedMillis.toRelativeElapsedTime()}"
}

private fun Long.toRelativeElapsedTime(): String {
    val elapsedMinutes = this / MILLIS_PER_MINUTE
    val elapsedHours = this / MILLIS_PER_HOUR
    val elapsedDays = this / MILLIS_PER_DAY
    val elapsedWeeks = elapsedDays / DAYS_PER_WEEK
    val elapsedMonths = elapsedDays / DAYS_PER_MONTH
    val elapsedYears = elapsedDays / DAYS_PER_YEAR

    return when {
        elapsedMinutes < 1 -> "just now"
        elapsedHours < 1 -> elapsedMinutes.toElapsedLabel("minute")
        elapsedDays < 1 -> elapsedHours.toElapsedLabel("hour")
        elapsedDays < DAYS_PER_WEEK -> elapsedDays.toElapsedLabel("day")
        elapsedWeeks < WEEKS_PER_MONTH -> elapsedWeeks.toElapsedLabel("week")
        elapsedMonths < MONTHS_PER_YEAR -> elapsedMonths.toElapsedLabel("month")
        else -> elapsedYears.toElapsedLabel("year")
    }
}

private fun Long.toElapsedLabel(unit: String): String {
    return "$this $unit${if (this == 1L) "" else "s"} ago"
}

private const val MILLIS_PER_MINUTE = 60_000L
private const val MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE
private const val MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR
private const val DAYS_PER_WEEK = 7L
private const val WEEKS_PER_MONTH = 5L
private const val DAYS_PER_MONTH = 30L
private const val DAYS_PER_YEAR = 365L
private const val MONTHS_PER_YEAR = 12L
