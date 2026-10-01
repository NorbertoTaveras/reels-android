/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.account

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.norbertotaveras.reels.data.local.preferences.TmdbAccountDataStore
import com.norbertotaveras.reels.data.remote.account.TmdbAccountRemoteDataSource
import com.norbertotaveras.reels.data.remote.error.TmdbSessionExpiredException
import com.norbertotaveras.reels.data.remote.error.isTmdbSessionExpiredError
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import com.norbertotaveras.reels.data.remote.mapper.account.toTmdbAccountDataModel
import com.norbertotaveras.reels.data.remote.mapper.account.toTmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.data.remote.mapper.movie.toMovieDataModel
import com.norbertotaveras.reels.data.remote.paging.TmdbAccountMoviesPagingSource
import com.norbertotaveras.reels.data.remote.paging.TmdbAccountMovieListType
import com.norbertotaveras.reels.data.remote.paging.tmdbPagingConfig
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.model.account.TmdbAccountStateDataModel
import com.norbertotaveras.reels.domain.model.account.TmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.repository.account.TmdbAccountRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class TmdbAccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbAccountRemoteDataSource,
    private val accountDataStore: TmdbAccountDataStore,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TmdbAccountRepository {
    override fun observeAccountState(): Flow<TmdbAccountStateDataModel> {
        return accountDataStore.accountState
    }

    override suspend fun startAuthentication(): Result<String> {
        return accountResult {
            val requestToken = remoteDataSource.createRequestToken().requestToken
            accountDataStore.savePendingRequestToken(requestToken = requestToken)
            "$TMDB_AUTHENTICATE_URL/$requestToken"
        }
    }

    override suspend fun finishAuthentication(): Result<TmdbAccountStateDataModel> {
        return accountResult {
            val pendingRequestToken = accountDataStore.accountState
                .first()
                .pendingRequestToken
                ?: error("Start TMDB connection before finishing authentication.")

            val sessionId = remoteDataSource.createSession(
                requestToken = pendingRequestToken
            ).sessionId
            val account = remoteDataSource.getAccountDetails(
                sessionId = sessionId
            ).toTmdbAccountDataModel()

            accountDataStore.saveConnectedAccount(
                sessionId = sessionId,
                account = account
            )

            TmdbAccountStateDataModel(
                sessionId = sessionId,
                pendingRequestToken = null,
                account = account
            )
        }
    }

    override suspend fun disconnect(): Result<Unit> {
        return accountResult {
            accountDataStore.clearAccount()
        }
    }

    override suspend fun getMovieAccountState(
        movieId: Int
    ): Result<TmdbMovieAccountStateDataModel?> {
        return accountResult(clearAccountOnExpiredSession = true) {
            val accountState = accountDataStore.accountState.first()
            val sessionId = accountState.sessionId ?: return@accountResult null

            remoteDataSource.getMovieAccountState(
                movieId = movieId,
                sessionId = sessionId
            ).toTmdbMovieAccountStateDataModel()
        }
    }

    override fun getWatchlistMovies(
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return getAccountMovies(
            language = language,
            listType = TmdbAccountMovieListType.Watchlist
        )
    }

    override fun getRatedMovies(
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return getAccountMovies(
            language = language,
            listType = TmdbAccountMovieListType.Rated
        )
    }

    override suspend fun updateMovieWatchlist(
        movieId: Int,
        watchlist: Boolean
    ): Result<Unit> {
        return accountResult(clearAccountOnExpiredSession = true) {
            val accountState = requireConnectedAccountState()
            remoteDataSource.updateWatchlist(
                accountId = accountState.account?.accountId
                    ?: error("TMDB account details are unavailable."),
                sessionId = accountState.sessionId
                    ?: error("TMDB session is unavailable."),
                movieId = movieId,
                watchlist = watchlist
            )
        }
    }

    override suspend fun rateMovie(
        movieId: Int,
        rating: Double
    ): Result<Unit> {
        return accountResult(clearAccountOnExpiredSession = true) {
            val sessionId = requireConnectedAccountState().sessionId
                ?: error("TMDB session is unavailable.")
            remoteDataSource.rateMovie(
                movieId = movieId,
                sessionId = sessionId,
                rating = rating
            )
        }
    }

    override suspend fun deleteMovieRating(movieId: Int): Result<Unit> {
        return accountResult(clearAccountOnExpiredSession = true) {
            val sessionId = requireConnectedAccountState().sessionId
                ?: error("TMDB session is unavailable.")
            remoteDataSource.deleteMovieRating(
                movieId = movieId,
                sessionId = sessionId
            )
        }
    }

    private suspend fun requireConnectedAccountState(): TmdbAccountStateDataModel {
        val accountState = accountDataStore.accountState.first()
        if (!accountState.isConnected) {
            error("Connect a TMDB account before syncing this movie.")
        }
        return accountState
    }

    private fun getAccountMovies(
        language: String,
        listType: TmdbAccountMovieListType,
    ): Flow<PagingData<MovieDataModel>> {
        return accountDataStore.accountState.flatMapLatest { accountState ->
            val accountId = accountState.account?.accountId
            val sessionId = accountState.sessionId

            if (!accountState.isConnected || accountId == null || sessionId == null) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = tmdbPagingConfig(),
                    pagingSourceFactory = {
                        TmdbAccountMoviesPagingSource(
                            remoteDataSource = remoteDataSource,
                            accountId = accountId,
                            sessionId = sessionId,
                            language = language,
                            listType = listType,
                            onSessionExpired = {
                                accountDataStore.clearAccount()
                            }
                        )
                    }
                ).flow.map { pagingData ->
                    pagingData.map { movieDto -> movieDto.toMovieDataModel() }
                }
            }
        }
    }

    private suspend fun <T> accountResult(
        clearAccountOnExpiredSession: Boolean = false,
        block: suspend () -> T,
    ): Result<T> {
        return runCatching {
            withContext(ioDispatcher) {
                block()
            }
        }.fold(
            onSuccess = { value -> Result.success(value) },
            onFailure = { error ->
                if (clearAccountOnExpiredSession && error.isTmdbSessionExpiredError()) {
                    accountDataStore.clearAccount()
                    Result.failure(TmdbSessionExpiredException().toTmdbNetworkException())
                } else {
                    Result.failure(error.toTmdbNetworkException())
                }
            }
        )
    }

    private companion object {
        const val TMDB_AUTHENTICATE_URL = "https://www.themoviedb.org/authenticate"
    }
}
