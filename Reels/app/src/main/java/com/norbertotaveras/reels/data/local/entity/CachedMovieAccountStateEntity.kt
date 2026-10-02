/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "cached_movie_account_states",
    primaryKeys = ["accountId", "movieId"]
)
data class CachedMovieAccountStateEntity(
    val accountId: Int,
    val movieId: Int,
    val isWatchlisted: Boolean,
    val rating: Double?,
    val cachedAt: Long,
)
