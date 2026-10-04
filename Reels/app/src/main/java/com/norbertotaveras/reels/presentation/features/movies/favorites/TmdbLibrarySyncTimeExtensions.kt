/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

internal fun Long?.toTmdbSyncLabel(
    nowMillis: Long = System.currentTimeMillis(),
): String {
    val syncedAtMillis = this ?: return "Synced earlier"
    val elapsedMillis = (nowMillis - syncedAtMillis).coerceAtLeast(0L)
    val elapsedMinutes = elapsedMillis / MILLIS_PER_MINUTE
    val elapsedHours = elapsedMillis / MILLIS_PER_HOUR
    val elapsedDays = elapsedMillis / MILLIS_PER_DAY

    return when {
        elapsedMinutes < 1 -> "Synced just now"
        elapsedHours < 1 -> "Synced $elapsedMinutes min ago"
        elapsedDays < 1 -> "Synced $elapsedHours h ago"
        else -> "Synced $elapsedDays d ago"
    }
}

private const val MILLIS_PER_MINUTE = 60_000L
private const val MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE
private const val MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR
