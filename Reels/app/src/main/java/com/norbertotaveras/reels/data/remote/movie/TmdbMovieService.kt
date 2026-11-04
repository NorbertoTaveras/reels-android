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
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbMovieService {
    @GET("movie/{category}")
    suspend fun getMovies(
        @Path("category") category: String,
        @Query("language") language: String?,
        @Query("page") page: Int?,
    ): MoviesResponseDto

    @GET("trending/movie/{timeWindow}")
    suspend fun getTrendingMovies(
        @Path("timeWindow") timeWindow: String,
        @Query("language") language: String?,
    ): MoviesResponseDto

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
    ): MovieDetailsDto

    @GET("movie/{movieId}/credits")
    suspend fun getMovieCredits(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
    ): MovieCreditsDto

    @GET("movie/{movieId}/videos")
    suspend fun getMovieVideos(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
    ): MovieVideosDto

    @GET("movie/{movieId}/similar")
    suspend fun getSimilarMovies(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
        @Query("page") page: Int?,
    ): MoviesResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String?,
        @Query("page") page: Int?,
        @Query("include_adult") includeAdult: Boolean,
    ): MoviesResponseDto

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("language") language: String?,
        @Query("page") page: Int?,
        @Query("sort_by") sortBy: String,
        @Query("with_genres") genreId: Int?,
        @Query("primary_release_date.gte") releaseDateFrom: String?,
        @Query("primary_release_date.lte") releaseDateTo: String?,
        @Query("vote_average.gte") minimumRating: Int?,
        @Query("include_adult") includeAdult: Boolean,
        @Query("include_video") includeVideo: Boolean = false,
    ): MoviesResponseDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") language: String?,
    ): GenresResponseDto
}
