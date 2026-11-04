/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.plans.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_nights")
data class MovieNightEntity(
    @PrimaryKey val id: String,
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val scheduledAt: Long,
    val runtimeMinutes: Int,
    val reminderMinutesBefore: Int?,
    val venue: String?,
    val attendeeCount: Int?,
    val note: String?,
    val status: String,
    val startedAt: Long?,
    val pausedAt: Long?,
    val accumulatedPausedMillis: Long,
    val completedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)
