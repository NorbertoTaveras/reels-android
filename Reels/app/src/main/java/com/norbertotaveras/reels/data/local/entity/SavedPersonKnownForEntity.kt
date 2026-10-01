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
    tableName = "saved_person_known_for",
    primaryKeys = ["personId", "movieId"],
    foreignKeys = [
        ForeignKey(
            entity = SavedPersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["personId"])
    ]
)
data class SavedPersonKnownForEntity(
    val personId: Int,
    val movieId: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val character: String?,
    val voteAverage: Double,
    val popularity: Double,
    val position: Int
)
