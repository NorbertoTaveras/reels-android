/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "cached_content_status",
    primaryKeys = ["cacheKey", "contentType"]
)
data class CachedContentStatusEntity(
    val cacheKey: String,
    val contentType: String,
    val cachedAt: Long,
)
