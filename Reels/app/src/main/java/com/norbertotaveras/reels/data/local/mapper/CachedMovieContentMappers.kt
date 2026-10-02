/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.mapper

import com.norbertotaveras.reels.data.local.entity.CachedMovieCastEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieDetailsEntity
import com.norbertotaveras.reels.data.local.entity.CachedMovieVideoEntity
import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieCreditsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideoDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideosDataModel

fun MovieDetailsDataModel.toCachedMovieDetailsEntity(
    cacheKey: String,
    cachedAt: Long,
): CachedMovieDetailsEntity {
    return CachedMovieDetailsEntity(
        cacheKey = cacheKey,
        movieId = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        originalLanguage = originalLanguage,
        popularity = popularity,
        voteCount = voteCount,
        genreIds = genres.map { genre -> genre.id },
        genreNames = genres.map { genre -> genre.name },
        cachedAt = cachedAt
    )
}

fun CachedMovieDetailsEntity.toMovieDetailsDataModel(): MovieDetailsDataModel {
    return MovieDetailsDataModel(
        id = movieId,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        originalLanguage = originalLanguage,
        popularity = popularity,
        voteCount = voteCount,
        genres = genreIds.zip(genreNames) { id, name ->
            GenreDataModel(id = id, name = name)
        }
    )
}

fun MovieCreditsDataModel.toCachedMovieCastEntities(
    cacheKey: String,
    cachedAt: Long,
): List<CachedMovieCastEntity> {
    return cast.mapIndexed { index, member ->
        CachedMovieCastEntity(
            cacheKey = cacheKey,
            personId = member.id,
            name = member.name,
            characterName = member.characterName,
            profilePath = member.profilePath,
            position = index,
            cachedAt = cachedAt
        )
    }
}

fun List<CachedMovieCastEntity>.toMovieCreditsDataModel(
    movieId: Int,
): MovieCreditsDataModel {
    return MovieCreditsDataModel(
        id = movieId,
        cast = map { member ->
            CastMemberDataModel(
                id = member.personId,
                name = member.name,
                characterName = member.characterName,
                profilePath = member.profilePath
            )
        }
    )
}

fun MovieVideosDataModel.toCachedMovieVideoEntities(
    cacheKey: String,
    cachedAt: Long,
): List<CachedMovieVideoEntity> {
    return videos.mapIndexed { index, video ->
        CachedMovieVideoEntity(
            cacheKey = cacheKey,
            videoId = video.id,
            name = video.name,
            key = video.key,
            site = video.site,
            type = video.type,
            official = video.official,
            position = index,
            cachedAt = cachedAt
        )
    }
}

fun List<CachedMovieVideoEntity>.toMovieVideosDataModel(): MovieVideosDataModel {
    return MovieVideosDataModel(
        videos = map { video ->
            MovieVideoDataModel(
                id = video.videoId,
                name = video.name,
                key = video.key,
                site = video.site,
                type = video.type,
                official = video.official
            )
        }
    )
}
