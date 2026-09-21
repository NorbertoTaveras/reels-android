package com.norbertotaveras.reels.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException
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

@RequiresApi(Build.VERSION_CODES.O)
fun String?.toRelativeReleaseAge(
    now: LocalDate = LocalDate.now()
): String? {
    val releaseDate = try {
        LocalDate.parse(this ?: return null)
    } catch (_: DateTimeParseException) {
        return null
    }

    val period = Period.between(releaseDate, now)

    if (releaseDate.isAfter(now)) {
        val futurePeriod = Period.between(now, releaseDate)

        return when {
            futurePeriod.years > 1 -> "in ${futurePeriod.years} years"
            futurePeriod.years == 1 -> "in 1 year"
            futurePeriod.months > 1 -> "in ${futurePeriod.months} months"
            futurePeriod.months == 1 -> "in 1 month"
            futurePeriod.days > 1 -> "in ${futurePeriod.days} days"
            futurePeriod.days == 1 -> "tomorrow"
            else -> null
        }
    }

    return when {
        period.years > 1 -> "${period.years} years ago"
        period.years == 1 -> "1 year ago"
        period.months > 1 -> "${period.months} months ago"
        period.months == 1 -> "1 month ago"
        period.days > 1 -> "${period.days} days ago"
        period.days == 1 -> "1 day ago"
        period.days == 0 -> "Released today"
        else -> null
    }
}

private const val TMDB_DATE_PATTERN = "yyyy-MM-dd"
private const val DISPLAY_DATE_PATTERN = "MMMM d, yyyy"
