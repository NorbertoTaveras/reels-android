/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.mapper

import com.norbertotaveras.reels.data.local.entity.CachedMovieAccountStateEntity
import com.norbertotaveras.reels.domain.model.account.TmdbMovieAccountStateDataModel

fun TmdbMovieAccountStateDataModel.toCachedMovieAccountStateEntity(
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

fun CachedMovieAccountStateEntity.toTmdbMovieAccountStateDataModel(): TmdbMovieAccountStateDataModel {
    return TmdbMovieAccountStateDataModel(
        movieId = movieId,
        isWatchlisted = isWatchlisted,
        rating = rating
    )
}
