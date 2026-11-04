/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun String?.toReleaseYear(): String? {
    return this
        ?.takeIf { it.length >= 4 }
        ?.take(4)
}

fun String?.toDisplayTmdbDate(): String? {
    val value = this?.takeIf { it.isNotBlank() } ?: return null
    val parser = SimpleDateFormat(TMDB_DATE_PATTERN, Locale.US)
    val formatter = SimpleDateFormat(DISPLAY_DATE_PATTERN, Locale.US)

    return try {
        formatter.format(parser.parse(value) ?: return value)
    } catch (_: ParseException) {
        value
    }
}

fun String?.toDisplayAge(): String? {
    val value = this?.takeIf { it.isNotBlank() } ?: return null
    val parser = SimpleDateFormat(TMDB_DATE_PATTERN, Locale.US)
    val birthday = try {
        parser.parse(value) ?: return null
    } catch (_: ParseException) {
        return null
    }

    val birthDate = Calendar.getInstance().apply { time = birthday }
    val today = Calendar.getInstance()

    var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    return "$age years old"
}

fun String?.toRelativeReleaseAge(
    now: Calendar = Calendar.getInstance()
): String? {
    val value = this?.takeIf { it.isNotBlank() } ?: return null
    val parser = SimpleDateFormat(TMDB_DATE_PATTERN, Locale.US)
    val releaseDate = try {
        parser.parse(value) ?: return null
    } catch (_: ParseException) {
        return null
    }

    val release = Calendar.getInstance().apply { time = releaseDate }
    val daysBetween = daysBetween(start = now, end = release)
    val monthsBetween = monthsBetween(start = now, end = release)
    val yearsBetween = yearsBetween(start = now, end = release)

    if (daysBetween > 0) {
        return when {
            yearsBetween > 1 -> "in $yearsBetween years"
            yearsBetween == 1 -> "in 1 year"
            monthsBetween > 1 -> "in $monthsBetween months"
            monthsBetween == 1 -> "in 1 month"
            daysBetween > 1 -> "in $daysBetween days"
            else -> "tomorrow"
        }
    }

    val elapsedDays = -daysBetween
    val elapsedMonths = -monthsBetween
    val elapsedYears = -yearsBetween

    return when {
        elapsedYears > 1 -> "$elapsedYears years ago"
        elapsedYears == 1 -> "1 year ago"
        elapsedMonths > 1 -> "$elapsedMonths months ago"
        elapsedMonths == 1 -> "1 month ago"
        elapsedDays > 1 -> "$elapsedDays days ago"
        elapsedDays == 1 -> "1 day ago"
        elapsedDays == 0 -> "Released today"
        else -> null
    }
}

private fun daysBetween(
    start: Calendar,
    end: Calendar
): Int {
    val startDay = start.normalizedDay()
    val endDay = end.normalizedDay()
    val millisBetween = endDay.timeInMillis - startDay.timeInMillis
    return (millisBetween / MILLIS_PER_DAY).toInt()
}

private fun monthsBetween(
    start: Calendar,
    end: Calendar
): Int {
    val months = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * MONTHS_PER_YEAR +
        (end.get(Calendar.MONTH) - start.get(Calendar.MONTH))
    return when {
        months > 0 && end.get(Calendar.DAY_OF_MONTH) < start.get(Calendar.DAY_OF_MONTH) -> months - 1
        months < 0 && end.get(Calendar.DAY_OF_MONTH) > start.get(Calendar.DAY_OF_MONTH) -> months + 1
        else -> months
    }
}

private fun yearsBetween(
    start: Calendar,
    end: Calendar
): Int {
    return monthsBetween(start = start, end = end) / MONTHS_PER_YEAR
}

private fun Calendar.normalizedDay(): Calendar {
    return (clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

private const val TMDB_DATE_PATTERN = "yyyy-MM-dd"
private const val DISPLAY_DATE_PATTERN = "MMMM d, yyyy"
private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
private const val MONTHS_PER_YEAR = 12
