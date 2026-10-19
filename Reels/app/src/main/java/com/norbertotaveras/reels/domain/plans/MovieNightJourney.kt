/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans

import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import kotlin.math.roundToInt

object MovieNightJourney {
    const val COUNTDOWN_WINDOW_MILLIS = 60 * 60_000L
    const val UPDATE_INTERVAL_MILLIS = 60_000L
}

fun MovieNightPlan.countdownProgress(now: Long = System.currentTimeMillis()): Int {
    val countdownStart = scheduledAt - MovieNightJourney.COUNTDOWN_WINDOW_MILLIS
    val elapsed = (now - countdownStart).coerceAtLeast(0L)
    return elapsed.toPercentageOf(MovieNightJourney.COUNTDOWN_WINDOW_MILLIS)
}

fun MovieNightPlan.runtimeProgress(now: Long = System.currentTimeMillis()): Int {
    return runtimeElapsedMillis(now).toPercentageOf(runtimeDurationMillis())
}

fun MovieNightPlan.hasRuntimeEnded(now: Long = System.currentTimeMillis()): Boolean =
    runtimeElapsedMillis(now) >= runtimeDurationMillis()

fun MovieNightPlan.canStartJourney(now: Long = System.currentTimeMillis()): Boolean =
    scheduledAt - now in 0..MovieNightJourney.COUNTDOWN_WINDOW_MILLIS

private fun Long.toPercentageOf(total: Long): Int =
    (toDouble() / total * MAX_PROGRESS).roundToInt().coerceIn(0, MAX_PROGRESS)

private fun MovieNightPlan.runtimeElapsedMillis(now: Long): Long {
    val started = startedAt ?: return 0L
    val endPoint = pausedAt ?: now
    return (endPoint - started - accumulatedPausedMillis).coerceAtLeast(0L)
}

private fun MovieNightPlan.runtimeDurationMillis(): Long = runtimeMinutes * MILLIS_PER_MINUTE

private const val MILLIS_PER_MINUTE = 60_000L
private const val MAX_PROGRESS = 100
