/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

internal fun Long?.toCachedVersionLabel(
    nowMillis: Long = System.currentTimeMillis(),
): String {
    val cachedAt = this ?: return "Viewing cached version"
    val elapsedMillis = (nowMillis - cachedAt).coerceAtLeast(0L)
    val elapsedMinutes = elapsedMillis / MILLIS_PER_MINUTE
    val elapsedHours = elapsedMillis / MILLIS_PER_HOUR
    val elapsedDays = elapsedMillis / MILLIS_PER_DAY

    val updated = when {
        elapsedMinutes < 1 -> "just now"
        elapsedHours < 1 -> "${elapsedMinutes}m ago"
        elapsedDays < 1 -> "${elapsedHours}h ago"
        else -> "${elapsedDays}d ago"
    }
    return "Viewing cached version \u00b7 Last updated $updated"
}

private const val MILLIS_PER_MINUTE = 60_000L
private const val MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE
private const val MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR
