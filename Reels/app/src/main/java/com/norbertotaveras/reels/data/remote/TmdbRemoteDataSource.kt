/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote

import com.norbertotaveras.reels.data.remote.dto.genre.GenresResponseDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbAccountDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbMovieAccountStateDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbRequestTokenDto
import com.norbertotaveras.reels.data.remote.dto.account.TmdbSessionDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieCreditsDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieDetailsDto
import com.norbertotaveras.reels.data.remote.dto.movie.MovieVideosDto
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.dto.person.PersonDetailsDto
import com.norbertotaveras.reels.data.remote.dto.person.PersonMovieCreditsDto

interface TmdbRemoteDataSource {
    suspend fun createRequestToken(): TmdbRequestTokenDto

    suspend fun createSession(requestToken: String): TmdbSessionDto

    suspend fun getAccountDetails(sessionId: String): TmdbAccountDto

    suspend fun getMovieAccountState(
        movieId: Int,
        sessionId: String
    ): TmdbMovieAccountStateDto

    suspend fun updateWatchlist(
        accountId: Int,
        sessionId: String,
        movieId: Int,
        watchlist: Boolean
    )

    suspend fun getWatchlistMovies(
        accountId: Int,
        sessionId: String,
        language: String? = null,
        page: Int? = null
    ): MoviesResponseDto

    suspend fun getRatedMovies(
        accountId: Int,
        sessionId: String,
        language: String? = null,
        page: Int? = null
    ): MoviesResponseDto

    suspend fun rateMovie(
        movieId: Int,
        sessionId: String,
        rating: Double
    )

    suspend fun deleteMovieRating(
        movieId: Int,
        sessionId: String
    )

    suspend fun getMovies(
        category: String,
        language: String? = null,
        page: Int? = null
    ): MoviesResponseDto

    suspend fun getTrendingMovies(
        timeWindow: String,
        language: String? = null
    ): MoviesResponseDto

    suspend fun getMovieDetails(
        movieId: Int,
        language: String? = null
    ): MovieDetailsDto

    suspend fun getMovieCredits(
        movieId: Int,
        language: String? = null
    ): MovieCreditsDto

    suspend fun getPersonDetails(
        personId: Int,
        language: String? = null
    ): PersonDetailsDto

    suspend fun getPersonMovieCredits(
        personId: Int,
        language: String? = null
    ): PersonMovieCreditsDto

    suspend fun getMovieVideos(
        movieId: Int,
        language: String? = null
    ): MovieVideosDto

    suspend fun getSimilarMovies(
        movieId: Int,
        language: String? = null,
        page: Int? = null
    ): MoviesResponseDto

    suspend fun searchMovies(
        query: String,
        language: String? = null,
        page: Int? = null,
        includeAdult: Boolean = false
    ): MoviesResponseDto

    suspend fun discoverMovies(
        language: String? = null,
        page: Int? = null,
        sortBy: String,
        genreId: Int? = null,
        releaseDateFrom: String? = null,
        releaseDateTo: String? = null,
        minimumRating: Int? = null,
        includeAdult: Boolean = false
    ): MoviesResponseDto

    suspend fun getMovieGenres(
        language: String? = null
    ): GenresResponseDto

}
