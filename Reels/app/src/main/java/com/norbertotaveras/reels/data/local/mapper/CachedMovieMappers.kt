/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.mapper

import com.norbertotaveras.reels.data.local.entity.CachedMovieEntity
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDto
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel

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
        cachedAt = cachedAt
    )
}

fun CachedMovieEntity.toMovieDataModel(): MovieDataModel {
    return MovieDataModel(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = genreIds
    )
}
