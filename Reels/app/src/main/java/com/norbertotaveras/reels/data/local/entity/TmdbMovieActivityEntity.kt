/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.account.TmdbMovieActivityDataModel

@Entity(
    tableName = "tmdb_movie_activities",
    primaryKeys = ["accountId", "listType", "movieId"],
    indices = [
        Index(value = ["accountId", "listType"])
    ]
)
data class TmdbMovieActivityEntity(
    val accountId: Int,
    val listType: String,
    val movieId: Int,
    val occurredAtMillis: Long,
)

internal fun TmdbMovieActivityEntity.toTmdbMovieActivityDataModel(): TmdbMovieActivityDataModel? {
    val libraryList = TmdbLibraryList.entries.firstOrNull { list -> list.name == listType }
        ?: return null
    return TmdbMovieActivityDataModel(
        movieId = movieId,
        listType = libraryList,
        occurredAtMillis = occurredAtMillis
    )
}
