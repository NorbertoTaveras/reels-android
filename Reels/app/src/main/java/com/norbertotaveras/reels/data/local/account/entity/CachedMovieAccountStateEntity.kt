/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.account.entity

import androidx.room.Entity
import com.norbertotaveras.reels.domain.account.model.TmdbMovieAccountStateDataModel

@Entity(
    tableName = "cached_movie_account_states",
    primaryKeys = ["accountId", "movieId"]
)
data class CachedMovieAccountStateEntity(
    val accountId: Int,
    val movieId: Int,
    val isWatchlisted: Boolean,
    val rating: Double?,
    val cachedAt: Long,
)

internal fun TmdbMovieAccountStateDataModel.toCachedMovieAccountStateEntity(
    accountId: Int,
    cachedAt: Long,
): CachedMovieAccountStateEntity {
    return CachedMovieAccountStateEntity(
        accountId = accountId,
        movieId = movieId,
        isWatchlisted = isWatchlisted,
        rating = rating,
        cachedAt = cachedAt
    )
}

internal fun CachedMovieAccountStateEntity.toTmdbMovieAccountStateDataModel(): TmdbMovieAccountStateDataModel {
    return TmdbMovieAccountStateDataModel(
        movieId = movieId,
        isWatchlisted = isWatchlisted,
        rating = rating
    )
}
