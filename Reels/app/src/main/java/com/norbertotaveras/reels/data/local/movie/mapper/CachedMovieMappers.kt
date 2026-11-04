/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.movie.mapper

import com.norbertotaveras.reels.data.local.movie.entity.CachedMovieEntity
import com.norbertotaveras.reels.data.remote.movie.dto.MovieDto
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel

fun MovieDto.toCachedMovieEntity(
    cachedAt: Long,
): CachedMovieEntity {
    return CachedMovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = genreIds,
        accountRating = accountRating,
        cachedAt = cachedAt
    )
}

fun MovieDataModel.toCachedMovieEntity(
    cachedAt: Long,
): CachedMovieEntity {
    return CachedMovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = genreIds,
        accountRating = accountRating,
        cachedAt = cachedAt
    )
}

fun CachedMovieEntity.toCachedMovieDataModel(): MovieDataModel {
    return MovieDataModel(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = genreIds,
        accountRating = accountRating
    )
}
