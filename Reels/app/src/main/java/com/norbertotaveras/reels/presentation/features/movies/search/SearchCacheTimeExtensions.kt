/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

private const val MILLIS_PER_MINUTE = 60_000L
private const val MILLIS_PER_HOUR = 60L * MILLIS_PER_MINUTE
private const val MILLIS_PER_DAY = 24L * MILLIS_PER_HOUR

internal fun Long?.toSearchCacheStatusLabel(
    isOffline: Boolean,
    nowMillis: Long = System.currentTimeMillis(),
): String {
    val age = this?.let { cachedAt ->
        val elapsed = (nowMillis - cachedAt).coerceAtLeast(0L)
        when {
            elapsed < MILLIS_PER_MINUTE -> "just now"
            elapsed < MILLIS_PER_HOUR -> {
                val minutes = elapsed / MILLIS_PER_MINUTE
                "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            }
            elapsed < MILLIS_PER_DAY -> {
                val hours = elapsed / MILLIS_PER_HOUR
                "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            }
            else -> {
                val days = elapsed / MILLIS_PER_DAY
                "$days ${if (days == 1L) "day" else "days"} ago"
            }
        }
    }

    val refreshMessage = if (isOffline) {
        "Reconnect to refresh"
    } else {
        "TMDB is temporarily unavailable"
    }
    return age?.let { "Searched $it · $refreshMessage" } ?: refreshMessage
}
