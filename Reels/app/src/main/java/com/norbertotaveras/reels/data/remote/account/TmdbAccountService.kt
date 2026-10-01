/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.account

import com.norbertotaveras.reels.data.remote.dto.account.TmdbAccountDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbCreateSessionRequestDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbMovieAccountStateDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbMutationResponseDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbRatingRequestDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbRequestTokenDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbSessionDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbWatchlistRequestDto
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbAccountService {
    @GET("authentication/token/new")
    suspend fun createRequestToken(): TmdbRequestTokenDto

    @POST("authentication/session/new")
    suspend fun createSession(
        @Body request: TmdbCreateSessionRequestDto,
    ): TmdbSessionDto

    @GET("account")
    suspend fun getAccountDetails(
        @Query("session_id") sessionId: String,
    ): TmdbAccountDto

    @GET("movie/{movieId}/account_states")
    suspend fun getMovieAccountState(
        @Path("movieId") movieId: Int,
        @Query("session_id") sessionId: String,
    ): TmdbMovieAccountStateDto

    @POST("account/{accountId}/watchlist")
    suspend fun updateWatchlist(
        @Path("accountId") accountId: Int,
        @Query("session_id") sessionId: String,
        @Body request: TmdbWatchlistRequestDto,
    ): TmdbMutationResponseDto

    @GET("account/{accountId}/watchlist/movies")
    suspend fun getWatchlistMovies(
        @Path("accountId") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("language") language: String?,
        @Query("page") page: Int?,
    ): MoviesResponseDto

    @GET("account/{accountId}/rated/movies")
    suspend fun getRatedMovies(
        @Path("accountId") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("language") language: String?,
        @Query("page") page: Int?,
    ): MoviesResponseDto

    @POST("movie/{movieId}/rating")
    suspend fun rateMovie(
        @Path("movieId") movieId: Int,
        @Query("session_id") sessionId: String,
        @Body request: TmdbRatingRequestDto,
    ): TmdbMutationResponseDto

    @DELETE("movie/{movieId}/rating")
    suspend fun deleteMovieRating(
        @Path("movieId") movieId: Int,
        @Query("session_id") sessionId: String,
    ): TmdbMutationResponseDto
}
