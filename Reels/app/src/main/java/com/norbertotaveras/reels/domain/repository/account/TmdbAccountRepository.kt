/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.repository.account

import androidx.paging.PagingData
import com.norbertotaveras.reels.domain.model.account.TmdbAccountStateDataModel
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.account.TmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.domain.model.account.TmdbMovieActivityDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import kotlinx.coroutines.flow.Flow

interface TmdbAccountRepository {
    fun observeAccountState(): Flow<TmdbAccountStateDataModel>

    fun observeMovieActivities(): Flow<List<TmdbMovieActivityDataModel>>

    fun observeLibraryLastSyncedAt(
        list: TmdbLibraryList,
        language: String,
    ): Flow<Long?>

    suspend fun startAuthentication(): Result<String>

    suspend fun finishAuthentication(): Result<TmdbAccountStateDataModel>

    suspend fun refreshAccount(): Result<TmdbAccountStateDataModel>

    suspend fun disconnect(): Result<Unit>

    suspend fun updateWatchlistSyncEnabled(enabled: Boolean)

    suspend fun updateRatingsSyncEnabled(enabled: Boolean)

    suspend fun getMovieAccountState(movieId: Int): Result<TmdbMovieAccountStateDataModel?>

    fun getWatchlistMovies(
        language: String,
    ): Flow<PagingData<MovieDataModel>>

    fun getRatedMovies(
        language: String,
    ): Flow<PagingData<MovieDataModel>>

    suspend fun updateMovieWatchlist(
        movieId: Int,
        watchlist: Boolean
    ): Result<TmdbMovieAccountStateDataModel>

    suspend fun rateMovie(
        movieId: Int,
        rating: Double
    ): Result<TmdbMovieAccountStateDataModel>

    suspend fun deleteMovieRating(movieId: Int): Result<TmdbMovieAccountStateDataModel>
}
