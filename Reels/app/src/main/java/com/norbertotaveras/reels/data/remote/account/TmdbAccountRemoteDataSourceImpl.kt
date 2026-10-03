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
import com.norbertotaveras.reels.data.remote.dto.account.TmdbRatingRequestDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbRequestTokenDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbSessionDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbWatchlistRequestDto
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import javax.inject.Inject

class TmdbAccountRemoteDataSourceImpl @Inject constructor(
    private val service: TmdbAccountService,
) : TmdbAccountRemoteDataSource {
    override suspend fun createRequestToken(): TmdbRequestTokenDto {
        return service.createRequestToken()
    }

    override suspend fun createSession(requestToken: String): TmdbSessionDto {
        return service.createSession(
            request = TmdbCreateSessionRequestDto(requestToken = requestToken)
        )
    }

    override suspend fun getAccountDetails(sessionId: String): TmdbAccountDto {
        return service.getAccountDetails(sessionId = sessionId)
    }

    override suspend fun getMovieAccountState(
        movieId: Int,
        sessionId: String,
    ): TmdbMovieAccountStateDto {
        return service.getMovieAccountState(
            movieId = movieId,
            sessionId = sessionId
        )
    }

    override suspend fun updateWatchlist(
        accountId: Int,
        sessionId: String,
        movieId: Int,
        watchlist: Boolean,
    ) {
        service.updateWatchlist(
            accountId = accountId,
            sessionId = sessionId,
            request = TmdbWatchlistRequestDto(
                mediaId = movieId,
                watchlist = watchlist
            )
        )
    }

    override suspend fun getWatchlistMovies(
        accountId: Int,
        sessionId: String,
        language: String?,
        page: Int?,
    ): MoviesResponseDto {
        return service.getWatchlistMovies(
            accountId = accountId,
            sessionId = sessionId,
            language = language,
            page = page,
            sortBy = CREATED_AT_DESCENDING
        )
    }

    override suspend fun getRatedMovies(
        accountId: Int,
        sessionId: String,
        language: String?,
        page: Int?,
    ): MoviesResponseDto {
        return service.getRatedMovies(
            accountId = accountId,
            sessionId = sessionId,
            language = language,
            page = page,
            sortBy = CREATED_AT_DESCENDING
        )
    }

    override suspend fun rateMovie(
        movieId: Int,
        sessionId: String,
        rating: Double,
    ) {
        service.rateMovie(
            movieId = movieId,
            sessionId = sessionId,
            request = TmdbRatingRequestDto(value = rating)
        )
    }

    override suspend fun deleteMovieRating(
        movieId: Int,
        sessionId: String,
    ) {
        service.deleteMovieRating(
            movieId = movieId,
            sessionId = sessionId
        )
    }

    private companion object {
        const val CREATED_AT_DESCENDING = "created_at.desc"
    }
}
