/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_releases")
data class TrackedReleaseEntity(
    @PrimaryKey val movieId: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val previousReleaseDate: String?,
    val reminder: String,
    val customReminderAt: Long?,
    val notificationsEnabled: Boolean,
    val trackedAt: Long,
    val updatedAt: Long,
)
