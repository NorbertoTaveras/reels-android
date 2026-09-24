/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote

import com.norbertotaveras.reels.data.remote.dto.genre.GenresResponseDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieCreditsDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDetailsDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieVideosDto
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.dto.person.PersonDetailsDto
import com.norbertotaveras.reels.data.remote.dto.person.PersonMovieCreditsDto
import javax.inject.Inject

class TmdbRemoteDataSourceImpl @Inject constructor(
    private val service: TmdbService
) : TmdbRemoteDataSource {
    override suspend fun getMovies(
        category: String,
        language: String?,
        page: Int?
    ): MoviesResponseDto {
        return service.getMovies(
            category = category,
            language = language,
            page = page
        )
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String?
    ): MovieDetailsDto {
        return service.getMovieDetails(
            movieId = movieId,
            language = language
        )
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String?
    ): MovieCreditsDto {
        return service.getMovieCredits(
            movieId = movieId,
            language = language
        )
    }

    override suspend fun getPersonDetails(
        personId: Int,
        language: String?
    ): PersonDetailsDto {
        return service.getPersonDetails(
            personId = personId,
            language = language
        )
    }

    override suspend fun getPersonMovieCredits(
        personId: Int,
        language: String?
    ): PersonMovieCreditsDto {
        return service.getPersonMovieCredits(
            personId = personId,
            language = language
        )
    }

    override suspend fun getMovieVideos(
        movieId: Int,
        language: String?
    ): MovieVideosDto {
        return service.getMovieVideos(
            movieId = movieId,
            language = language
        )
    }

    override suspend fun getSimilarMovies(
        movieId: Int,
        language: String?,
        page: Int?
    ): MoviesResponseDto {
        return service.getSimilarMovies(
            movieId = movieId,
            language = language,
            page = page
        )
    }

    override suspend fun searchMovies(
        query: String,
        language: String?,
        page: Int?,
        includeAdult: Boolean
    ): MoviesResponseDto {
        return service.searchMovies(
            query = query,
            language = language,
            page = page,
            includeAdult = includeAdult
        )
    }

    override suspend fun discoverMovies(
        language: String?,
        page: Int?,
        sortBy: String,
        genreId: Int?,
        year: Int?,
        minimumRating: Int?,
        includeAdult: Boolean
    ): MoviesResponseDto {
        return service.discoverMovies(
            language = language,
            page = page,
            sortBy = sortBy,
            genreId = genreId,
            year = year,
            minimumRating = minimumRating,
            includeAdult = includeAdult
        )
    }

    override suspend fun getMovieGenres(
        language: String?
    ): GenresResponseDto {
        return service.getMovieGenres(
            language = language
        )
    }
}
