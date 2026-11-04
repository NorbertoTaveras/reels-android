/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.account

import com.norbertotaveras.reels.data.remote.account.dto.TmdbAccountDto
import com.norbertotaveras.reels.data.remote.account.dto.TmdbMovieAccountStateDto
import com.norbertotaveras.reels.data.remote.account.dto.TmdbRequestTokenDto
import com.norbertotaveras.reels.data.remote.account.dto.TmdbSessionDto
import com.norbertotaveras.reels.data.remote.movie.dto.MoviesResponseDto

interface TmdbAccountRemoteDataSource {
    suspend fun createRequestToken(): TmdbRequestTokenDto

    suspend fun createSession(requestToken: String): TmdbSessionDto

    suspend fun deleteSession(sessionId: String)

    suspend fun getAccountDetails(sessionId: String): TmdbAccountDto

    suspend fun getMovieAccountState(
        movieId: Int,
        sessionId: String,
    ): TmdbMovieAccountStateDto

    suspend fun updateWatchlist(
        accountId: Int,
        sessionId: String,
        movieId: Int,
        watchlist: Boolean,
    )

    suspend fun getWatchlistMovies(
        accountId: Int,
        sessionId: String,
        language: String? = null,
        page: Int? = null,
    ): MoviesResponseDto

    suspend fun getRatedMovies(
        accountId: Int,
        sessionId: String,
        language: String? = null,
        page: Int? = null,
    ): MoviesResponseDto

    suspend fun rateMovie(
        movieId: Int,
        sessionId: String,
        rating: Double,
    )

    suspend fun deleteMovieRating(
        movieId: Int,
        sessionId: String,
    )
}
