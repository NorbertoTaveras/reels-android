/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "cached_movie_list_entries",
    primaryKeys = ["listKey", "movieId"],
    foreignKeys = [
        ForeignKey(
            entity = CachedMovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["movieId"]),
        Index(value = ["listKey", "position"])
    ]
)
data class CachedMovieListEntryEntity(
    val listKey: String,
    val movieId: Int,
    val position: Int,
    val accountRating: Double? = null,
)
