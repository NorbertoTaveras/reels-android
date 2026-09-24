/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote

import com.norbertotaveras.reels.data.remote.dto.genre.GenresResponseDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbAccountDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbCreateSessionRequestDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbRequestTokenDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbSessionDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieCreditsDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDetailsDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieVideosDto
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.dto.person.PersonDetailsDto
import com.norbertotaveras.reels.data.remote.dto.person.PersonMovieCreditsDto
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface TmdbService {
    @GET("authentication/token/new")
    suspend fun createRequestToken(): TmdbRequestTokenDto

    @POST("authentication/session/new")
    suspend fun createSession(
        @Body request: TmdbCreateSessionRequestDto
    ): TmdbSessionDto

    @GET("account")
    suspend fun getAccountDetails(
        @Query("session_id") sessionId: String
    ): TmdbAccountDto

    @GET("movie/{category}")
    suspend fun getMovies(
        @Path("category") category: String,
        @Query("language") language: String?,
        @Query("page") page: Int?
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

    @GET("person/{personId}")
    suspend fun getPersonDetails(
        @Path("personId") personId: Int,
        @Query("language") language: String?,
    ): PersonDetailsDto

    @GET("person/{personId}/movie_credits")
    suspend fun getPersonMovieCredits(
        @Path("personId") personId: Int,
        @Query("language") language: String?,
    ): PersonMovieCreditsDto

    @GET("movie/{movieId}/videos")
    suspend fun getMovieVideos(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
    ): MovieVideosDto

    @GET("movie/{movieId}/similar")
    suspend fun getSimilarMovies(
        @Path("movieId") movieId: Int,
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): MoviesResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String?,
        @Query("page") page: Int?,
        @Query("include_adult") includeAdult: Boolean = false
    ): MoviesResponseDto

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("language") language: String?,
        @Query("page") page: Int?,
        @Query("sort_by") sortBy: String,
        @Query("with_genres") genreId: Int?,
        @Query("primary_release_year") year: Int?,
        @Query("vote_average.gte") minimumRating: Int?,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false
    ): MoviesResponseDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("language") language: String?
    ): GenresResponseDto

}
