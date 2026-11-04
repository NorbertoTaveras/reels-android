/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.person.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_person_details")
data class CachedPersonDetailsEntity(
    @PrimaryKey
    val cacheKey: String,
    val personId: Int,
    val name: String,
    val biography: String,
    val birthday: String?,
    val placeOfBirth: String?,
    val profilePath: String?,
    val knownForDepartment: String?,
    val cachedAt: Long,
)
