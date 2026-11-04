/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.plans.model

data class MovieNightPlan(
    val id: String,
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val scheduledAt: Long,
    val runtimeMinutes: Int,
    val reminderMinutesBefore: Int? = 30,
    val venue: String? = null,
    val attendeeCount: Int? = null,
    val note: String? = null,
    val status: MovieNightStatus = MovieNightStatus.Upcoming,
    val startedAt: Long? = null,
    val pausedAt: Long? = null,
    val accumulatedPausedMillis: Long = 0L,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    init {
        require(runtimeMinutes in 1..600) { "Runtime must be between 1 and 600 minutes." }
        require(attendeeCount == null || attendeeCount > 0) { "Attendee count must be positive." }
    }
}
