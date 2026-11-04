/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.library.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_people")
data class SavedPersonEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val biography: String,
    val birthday: String?,
    val placeOfBirth: String?,
    val profilePath: String?,
    val knownForDepartment: String?,
    val savedAt: Long
)
