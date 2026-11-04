/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.person.entity

import androidx.room.Entity

@Entity(
    tableName = "cached_person_movie_credits",
    primaryKeys = ["cacheKey", "movieId"]
)
data class CachedPersonMovieCreditEntity(
    val cacheKey: String,
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val character: String?,
    val voteAverage: Double,
    val popularity: Double,
    val position: Int,
    val cachedAt: Long,
)
