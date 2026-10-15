/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

internal fun Long?.toTmdbSyncLabel(
    prefix: String = "Synced",
    missingLabel: String = "Synced earlier",
    nowMillis: Long = System.currentTimeMillis(),
): String {
    val syncedAtMillis = this ?: return missingLabel
    val elapsedMillis = (nowMillis - syncedAtMillis).coerceAtLeast(0L)
    val elapsedMinutes = elapsedMillis / MILLIS_PER_MINUTE
    val elapsedHours = elapsedMillis / MILLIS_PER_HOUR
    val elapsedDays = elapsedMillis / MILLIS_PER_DAY

    val elapsedLabel = when {
        elapsedMinutes < 1 -> "just now"
        elapsedHours < 1 -> "$elapsedMinutes min ago"
        elapsedDays < 1 -> "$elapsedHours h ago"
        else -> "$elapsedDays d ago"
    }

    return prefix
        .takeIf(String::isNotBlank)
        ?.let { "$it $elapsedLabel" }
        ?: elapsedLabel
}

private const val MILLIS_PER_MINUTE = 60_000L
private const val MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE
private const val MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR
