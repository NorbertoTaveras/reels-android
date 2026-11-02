/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.entity

import androidx.room.Entity
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel

@Entity(
    tableName = "cached_genres",
    primaryKeys = ["language", "genreId"]
)
data class CachedGenreEntity(
    val language: String,
    val genreId: Int,
    val name: String,
    val cachedAt: Long,
)

internal fun GenreDataModel.toCachedGenreEntity(
    language: String,
    cachedAt: Long,
): CachedGenreEntity {
    return CachedGenreEntity(
        language = language,
        genreId = id,
        name = name,
        cachedAt = cachedAt
    )
}

internal fun CachedGenreEntity.toGenreDataModel(): GenreDataModel {
    return GenreDataModel(
        id = genreId,
        name = name
    )
}
