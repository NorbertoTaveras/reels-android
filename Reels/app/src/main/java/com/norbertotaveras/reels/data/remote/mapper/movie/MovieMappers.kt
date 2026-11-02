/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.mapper.movie

import com.norbertotaveras.reels.data.remote.dto.movie.MovieCreditsDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDetailsDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieVideoDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieVideosDto
import com.norbertotaveras.reels.data.remote.dto.cast.toCastMemberDataModel
import com.norbertotaveras.reels.data.remote.dto.genre.toGenreDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieCreditsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideoDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideosDataModel

fun MovieDto.toMovieDataModel(): MovieDataModel {
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

fun MovieDetailsDto.toMovieDetailsDataModel(): MovieDetailsDataModel {
    return MovieDetailsDataModel(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        voteAverage = voteAverage,
        originalLanguage = originalLanguage,
        popularity = popularity ?: 0.0,
        voteCount = voteCount ?: 0,
        genres = genres.map { genre -> genre.toGenreDataModel() }
    )
}

fun MovieCreditsDto.toMovieCreditsDataModel(): MovieCreditsDataModel {
    return MovieCreditsDataModel(
        id = id,
        cast = cast.map { member -> member.toCastMemberDataModel() }
    )
}

fun MovieVideosDto.toMovieVideosDataModel(): MovieVideosDataModel {
    return MovieVideosDataModel(
        videos = results
            .orEmpty()
            .filter { video -> video.id != null && !video.key.isNullOrBlank() }
            .map { video -> video.toMovieVideoDataModel() }
            .sortedWith(
                compareByDescending<MovieVideoDataModel> { video -> video.official }
                    .thenBy { video -> video.type.sortOrder() }
            )
    )
}

private fun MovieVideoDto.toMovieVideoDataModel(): MovieVideoDataModel {
    return MovieVideoDataModel(
        id = requireNotNull(id),
        name = name.orEmpty(),
        key = key.orEmpty(),
        site = site.orEmpty(),
        type = type.orEmpty(),
        official = official ?: false
    )
}

private fun String.sortOrder(): Int {
    return when (lowercase()) {
        "trailer" -> 0
        "teaser" -> 1
        "clip" -> 2
        "featurette" -> 3
        else -> 4
    }
}
