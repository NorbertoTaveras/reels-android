/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.movie.entity

import androidx.room.Entity

@Entity(
    tableName = "cached_movie_cast",
    primaryKeys = ["cacheKey", "personId"]
)
data class CachedMovieCastEntity(
    val cacheKey: String,
    val personId: Int,
    val name: String,
    val characterName: String,
    val profilePath: String?,
    val position: Int,
    val cachedAt: Long,
)
