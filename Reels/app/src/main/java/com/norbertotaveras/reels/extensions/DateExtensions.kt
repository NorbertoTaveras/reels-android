package com.norbertotaveras.reels.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException

fun String?.toReleaseYear(): String? {
    return this
        ?.takeIf { it.length >= 4 }
        ?.take(4)
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

    return when {
        period.years > 1 -> "${period.years} years ago"
        period.years == 1 -> "1 year ago"
        period.months > 1 -> "${period.months} months ago"
        period.months == 1 -> "1 month ago"
        period.days > 1 -> "${period.days} days ago"
        period.days == 1 -> "1 day ago"
        period.days == 0 -> "Released today"
        period.days < 0 -> "Coming soon"
        else -> null
    }
}