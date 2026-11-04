/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.movie.entity

import androidx.room.Entity

@Entity(
    tableName = "cached_movie_videos",
    primaryKeys = ["cacheKey", "videoId"]
)
data class CachedMovieVideoEntity(
    val cacheKey: String,
    val videoId: String,
    val name: String,
    val key: String,
    val site: String,
    val type: String,
    val official: Boolean,
    val position: Int,
    val cachedAt: Long,
)
