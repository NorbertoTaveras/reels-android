/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_movie_details")
data class CachedMovieDetailsEntity(
    @PrimaryKey
    val cacheKey: String,
    val movieId: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val runtime: Int?,
    val voteAverage: Double,
    val originalLanguage: String?,
    val popularity: Double,
    val voteCount: Int,
    val genreIds: List<Int>,
    val genreNames: List<String>,
    val cachedAt: Long,
)
