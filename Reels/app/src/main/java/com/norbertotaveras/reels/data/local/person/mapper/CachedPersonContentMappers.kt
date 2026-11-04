/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.person.mapper

import com.norbertotaveras.reels.data.local.person.entity.CachedPersonDetailsEntity
import com.norbertotaveras.reels.data.local.person.entity.CachedPersonMovieCreditEntity
import com.norbertotaveras.reels.domain.person.model.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditDataModel
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditsDataModel

fun PersonDetailsDataModel.toCachedPersonDetailsEntity(
    cacheKey: String,
    cachedAt: Long,
): CachedPersonDetailsEntity {
    return CachedPersonDetailsEntity(
        cacheKey = cacheKey,
        personId = id,
        name = name,
        biography = biography,
        birthday = birthday,
        placeOfBirth = placeOfBirth,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment,
        cachedAt = cachedAt
    )
}

fun CachedPersonDetailsEntity.toPersonDetailsDataModel(): PersonDetailsDataModel {
    return PersonDetailsDataModel(
        id = personId,
        name = name,
        biography = biography,
        birthday = birthday,
        placeOfBirth = placeOfBirth,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment
    )
}

fun PersonMovieCreditsDataModel.toCachedPersonMovieCreditEntities(
    cacheKey: String,
    cachedAt: Long,
): List<CachedPersonMovieCreditEntity> {
    return cast.mapIndexed { index, movie ->
        CachedPersonMovieCreditEntity(
            cacheKey = cacheKey,
            movieId = movie.id,
            title = movie.title,
            posterPath = movie.posterPath,
            releaseDate = movie.releaseDate,
            character = movie.character,
            voteAverage = movie.voteAverage,
            popularity = movie.popularity,
            position = index,
            cachedAt = cachedAt
        )
    }
}

fun List<CachedPersonMovieCreditEntity>.toPersonMovieCreditsDataModel(): PersonMovieCreditsDataModel {
    return PersonMovieCreditsDataModel(
        cast = map { movie ->
            PersonMovieCreditDataModel(
                id = movie.movieId,
                title = movie.title,
                posterPath = movie.posterPath,
                releaseDate = movie.releaseDate,
                character = movie.character,
                voteAverage = movie.voteAverage,
                popularity = movie.popularity
            )
        }
    )
}
