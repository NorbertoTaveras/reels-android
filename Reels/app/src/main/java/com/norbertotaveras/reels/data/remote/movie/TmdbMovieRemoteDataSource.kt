/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.movie

import com.norbertotaveras.reels.data.remote.movie.dto.GenresResponseDto
import com.norbertotaveras.reels.data.remote.movie.dto.MovieCreditsDto
import com.norbertotaveras.reels.data.remote.movie.dto.MovieDetailsDto
import com.norbertotaveras.reels.data.remote.movie.dto.MovieVideosDto
import com.norbertotaveras.reels.data.remote.movie.dto.MoviesResponseDto

interface TmdbMovieRemoteDataSource {
    suspend fun getMovies(
        category: String,
        language: String? = null,
        page: Int? = null,
    ): MoviesResponseDto

    suspend fun getTrendingMovies(
        timeWindow: String,
        language: String? = null,
    ): MoviesResponseDto

    suspend fun getMovieDetails(
        movieId: Int,
        language: String? = null,
    ): MovieDetailsDto

    suspend fun getMovieCredits(
        movieId: Int,
        language: String? = null,
    ): MovieCreditsDto

    suspend fun getMovieVideos(
        movieId: Int,
        language: String? = null,
    ): MovieVideosDto

    suspend fun getSimilarMovies(
        movieId: Int,
        language: String? = null,
        page: Int? = null,
    ): MoviesResponseDto

    suspend fun searchMovies(
        query: String,
        language: String? = null,
        page: Int? = null,
        includeAdult: Boolean = false,
    ): MoviesResponseDto

    suspend fun discoverMovies(
        language: String? = null,
        page: Int? = null,
        parameters: DiscoverMovieRequestParameters,
        includeAdult: Boolean = false,
    ): MoviesResponseDto

    suspend fun getMovieGenres(
        language: String? = null,
    ): GenresResponseDto
}
