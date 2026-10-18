/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.plans

data class TrackedRelease(
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val previousReleaseDate: String? = null,
    val reminder: ReleaseReminder = ReleaseReminder.OneDayBefore,
    val customReminderAt: Long? = null,
    val notificationsEnabled: Boolean = true,
    val trackedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
