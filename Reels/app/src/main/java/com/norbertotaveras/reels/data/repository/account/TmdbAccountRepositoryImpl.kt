/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.account

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.map
import com.norbertotaveras.reels.data.local.dao.CachedContentDao
import com.norbertotaveras.reels.data.local.dao.CachedMovieDao
import com.norbertotaveras.reels.data.local.dao.TmdbMovieActivityDao
import com.norbertotaveras.reels.data.local.db.ReelsDatabase
import com.norbertotaveras.reels.data.local.entity.TmdbMovieActivityEntity
import com.norbertotaveras.reels.data.local.mapper.toCachedMovieAccountStateEntity
import com.norbertotaveras.reels.data.local.mapper.toTmdbMovieActivityDataModel
import com.norbertotaveras.reels.data.local.mapper.toTmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.data.local.preferences.TmdbAccountDataStore
import com.norbertotaveras.reels.data.mapper.movie.toCachedMovieDataModel
import com.norbertotaveras.reels.data.remote.account.TmdbAccountRemoteDataSource
import com.norbertotaveras.reels.data.remote.dto.movie.MoviesResponseDto
import com.norbertotaveras.reels.data.remote.error.TmdbSessionExpiredException
import com.norbertotaveras.reels.data.remote.error.isTmdbSessionExpiredError
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import com.norbertotaveras.reels.data.remote.mapper.account.toTmdbAccountDataModel
import com.norbertotaveras.reels.data.remote.mapper.account.toTmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.data.remote.paging.CachedMovieListRemoteMediator
import com.norbertotaveras.reels.data.remote.paging.MovieCachePolicy
import com.norbertotaveras.reels.data.remote.paging.tmdbPagingConfig
import com.norbertotaveras.reels.data.remote.paging.tmdbLibraryCacheKey
import com.norbertotaveras.reels.data.repository.repositoryResult
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.model.account.TmdbAccountStateDataModel
import com.norbertotaveras.reels.domain.model.account.TmdbAuthenticationCallback
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.account.TmdbMovieAccountStateDataModel
import com.norbertotaveras.reels.domain.model.account.TmdbMovieActivityDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.repository.account.TmdbAccountRepository
import javax.inject.Inject
import javax.inject.Singleton
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

@Singleton
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
class TmdbAccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbAccountRemoteDataSource,
    private val accountDataStore: TmdbAccountDataStore,
    private val database: ReelsDatabase,
    private val cachedMovieDao: CachedMovieDao,
    private val cachedContentDao: CachedContentDao,
    private val tmdbMovieActivityDao: TmdbMovieActivityDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TmdbAccountRepository {
    override fun observeAccountState(): Flow<TmdbAccountStateDataModel> {
        return accountDataStore.accountState
    }

    override fun observeMovieActivities(): Flow<List<TmdbMovieActivityDataModel>> {
        return accountDataStore.accountState.flatMapLatest { accountState ->
            val accountId = accountState.account?.accountId
            if (!accountState.isConnected || accountId == null) {
                flowOf(emptyList())
            } else {
                tmdbMovieActivityDao.observeActivities(accountId = accountId).map { activities ->
                    activities.mapNotNull { activity ->
                        activity.toTmdbMovieActivityDataModel()
                    }
                }
            }
        }
    }

    override suspend fun startAuthentication(): Result<String> {
        return accountResult {
            val requestToken = remoteDataSource.createRequestToken().requestToken
            accountDataStore.savePendingRequestToken(requestToken = requestToken)
            val encodedCallback = URLEncoder.encode(
                TmdbAuthenticationCallback.URI,
                StandardCharsets.UTF_8.toString()
            )
            "$TMDB_AUTHENTICATE_URL/$requestToken?redirect_to=$encodedCallback"
        }
    }

    override suspend fun finishAuthentication(): Result<TmdbAccountStateDataModel> {
        return accountResult {
            val currentAccountState = accountDataStore.accountState.first()
            if (currentAccountState.isConnected) {
                return@accountResult currentAccountState
            }

            val pendingRequestToken = currentAccountState.pendingRequestToken
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
            val sessionId = accountDataStore.accountState.first().sessionId
            try {
                if (!sessionId.isNullOrBlank()) {
                    remoteDataSource.deleteSession(sessionId = sessionId)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // Local disconnect must remain available when TMDB cannot be reached.
            } finally {
                accountDataStore.clearAccount()
            }
        }
    }

    override suspend fun getMovieAccountState(
        movieId: Int
    ): Result<TmdbMovieAccountStateDataModel?> {
        val accountState = accountDataStore.accountState.first()
        val accountId = accountState.account?.accountId ?: return Result.success(null)
        val sessionId = accountState.sessionId ?: return Result.success(null)
        val remoteResult = accountResult(clearAccountOnExpiredSession = true) {
            remoteDataSource.getMovieAccountState(movieId = movieId, sessionId = sessionId)
                .toTmdbMovieAccountStateDataModel()
                .also { state -> cacheMovieAccountState(accountId = accountId, state = state) }
        }

        return if (remoteResult.isSuccess) {
            remoteResult
        } else {
            val cached = cachedContentDao.getMovieAccountState(
                accountId = accountId,
                movieId = movieId
            )?.toTmdbMovieAccountStateDataModel()
            cached?.let { state -> Result.success(state) } ?: remoteResult
        }
    }

    override fun getWatchlistMovies(
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return getAccountMovies(
            list = TmdbLibraryList.Watchlist,
            language = language,
            loadPage = { accountId, sessionId, page ->
                remoteDataSource.getWatchlistMovies(
                    accountId = accountId,
                    sessionId = sessionId,
                    language = language,
                    page = page
                )
            }
        )
    }

    override fun getRatedMovies(
        language: String,
    ): Flow<PagingData<MovieDataModel>> {
        return getAccountMovies(
            list = TmdbLibraryList.Rated,
            language = language,
            loadPage = { accountId, sessionId, page ->
                remoteDataSource.getRatedMovies(
                    accountId = accountId,
                    sessionId = sessionId,
                    language = language,
                    page = page
                )
            }
        )
    }

    override suspend fun updateMovieWatchlist(
        movieId: Int,
        watchlist: Boolean
    ): Result<TmdbMovieAccountStateDataModel> {
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
            val accountId = accountState.account?.accountId
                ?: error("TMDB account details are unavailable.")
            val cached = cachedContentDao.getMovieAccountState(accountId, movieId)
                ?.toTmdbMovieAccountStateDataModel()
                ?: TmdbMovieAccountStateDataModel(
                    movieId = movieId,
                    isWatchlisted = false,
                    rating = null
                )
            val updatedState = cached.copy(isWatchlisted = watchlist)
            cacheMovieAccountState(accountId = accountId, state = updatedState)
            updateMovieActivity(
                accountId = accountId,
                movieId = movieId,
                listType = TmdbLibraryList.Watchlist,
                isActive = watchlist
            )
            cachedMovieDao.invalidateAccountLists(accountId = accountId)
            updatedState
        }
    }

    override suspend fun rateMovie(
        movieId: Int,
        rating: Double
    ): Result<TmdbMovieAccountStateDataModel> {
        return accountResult(clearAccountOnExpiredSession = true) {
            val accountState = requireConnectedAccountState()
            val sessionId = accountState.sessionId
                ?: error("TMDB session is unavailable.")
            remoteDataSource.rateMovie(
                movieId = movieId,
                sessionId = sessionId,
                rating = rating
            )
            val accountId = accountState.account?.accountId
                ?: error("TMDB account details are unavailable.")
            val cached = cachedContentDao.getMovieAccountState(accountId, movieId)
                ?.toTmdbMovieAccountStateDataModel()
                ?: TmdbMovieAccountStateDataModel(
                    movieId = movieId,
                    isWatchlisted = false,
                    rating = null
                )
            val updatedState = cached.copy(rating = rating)
            cacheMovieAccountState(accountId = accountId, state = updatedState)
            updateMovieActivity(
                accountId = accountId,
                movieId = movieId,
                listType = TmdbLibraryList.Rated,
                isActive = true
            )
            cachedMovieDao.invalidateAccountLists(accountId = accountId)
            updatedState
        }
    }

    override suspend fun deleteMovieRating(
        movieId: Int
    ): Result<TmdbMovieAccountStateDataModel> {
        return accountResult(clearAccountOnExpiredSession = true) {
            val accountState = requireConnectedAccountState()
            val sessionId = accountState.sessionId
                ?: error("TMDB session is unavailable.")
            remoteDataSource.deleteMovieRating(
                movieId = movieId,
                sessionId = sessionId
            )
            val accountId = accountState.account?.accountId
                ?: error("TMDB account details are unavailable.")
            val cached = cachedContentDao.getMovieAccountState(accountId, movieId)
                ?.toTmdbMovieAccountStateDataModel()
                ?: TmdbMovieAccountStateDataModel(
                    movieId = movieId,
                    isWatchlisted = false,
                    rating = null
                )
            val updatedState = cached.copy(rating = null)
            cacheMovieAccountState(accountId = accountId, state = updatedState)
            updateMovieActivity(
                accountId = accountId,
                movieId = movieId,
                listType = TmdbLibraryList.Rated,
                isActive = false
            )
            cachedMovieDao.invalidateAccountLists(accountId = accountId)
            updatedState
        }
    }

    private suspend fun requireConnectedAccountState(): TmdbAccountStateDataModel {
        val accountState = accountDataStore.accountState.first()
        if (!accountState.isConnected) {
            error("Connect a TMDB account before syncing this movie.")
        }
        return accountState
    }

    private suspend fun cacheMovieAccountState(
        accountId: Int,
        state: TmdbMovieAccountStateDataModel,
    ) {
        cachedContentDao.upsertMovieAccountState(
            state.toCachedMovieAccountStateEntity(
                accountId = accountId,
                cachedAt = System.currentTimeMillis()
            )
        )
    }

    private suspend fun updateMovieActivity(
        accountId: Int,
        movieId: Int,
        listType: TmdbLibraryList,
        isActive: Boolean,
    ) {
        if (isActive) {
            tmdbMovieActivityDao.upsertActivity(
                TmdbMovieActivityEntity(
                    accountId = accountId,
                    listType = listType.name,
                    movieId = movieId,
                    occurredAtMillis = System.currentTimeMillis()
                )
            )
        } else {
            tmdbMovieActivityDao.deleteActivity(
                accountId = accountId,
                listType = listType.name,
                movieId = movieId
            )
        }
    }

    private fun getAccountMovies(
        list: TmdbLibraryList,
        language: String,
        loadPage: suspend (
            accountId: Int,
            sessionId: String,
            page: Int,
        ) -> MoviesResponseDto,
    ): Flow<PagingData<MovieDataModel>> {
        return accountDataStore.accountState.flatMapLatest { accountState ->
            val accountId = accountState.account?.accountId
            val sessionId = accountState.sessionId

            if (!accountState.isConnected || accountId == null || sessionId == null) {
                flowOf(PagingData.empty())
            } else {
                val listKey = tmdbLibraryCacheKey(
                    accountId = accountId,
                    list = list,
                    language = language
                )
                Pager(
                    config = tmdbPagingConfig(),
                    remoteMediator = CachedMovieListRemoteMediator(
                        database = database,
                        cachedMovieDao = cachedMovieDao,
                        listKey = listKey,
                        cacheTimeoutMillis = MovieCachePolicy.ACCOUNT_CACHE_TIMEOUT_MILLIS,
                        loadPage = { page -> loadPage(accountId, sessionId, page) },
                        transformError = { error ->
                            if (error.isTmdbSessionExpiredError()) {
                                accountDataStore.clearAccount()
                                TmdbSessionExpiredException()
                            } else {
                                error.toTmdbNetworkException()
                            }
                        }
                    ),
                    pagingSourceFactory = {
                        cachedMovieDao.pagingSource(listKey = listKey)
                    }
                ).flow.map { pagingData ->
                    pagingData.map { movie -> movie.toCachedMovieDataModel() }
                }
            }
        }
    }

    private suspend fun <T> accountResult(
        clearAccountOnExpiredSession: Boolean = false,
        block: suspend () -> T,
    ): Result<T> {
        return repositoryResult(
            dispatcher = ioDispatcher,
            transformError = { error ->
                if (clearAccountOnExpiredSession && error.isTmdbSessionExpiredError()) {
                    accountDataStore.clearAccount()
                    TmdbSessionExpiredException().toTmdbNetworkException()
                } else {
                    error.toTmdbNetworkException()
                }
            }
        ) {
            block()
        }
    }

    private companion object {
        const val TMDB_AUTHENTICATE_URL = "https://www.themoviedb.org/authenticate"
    }
}
