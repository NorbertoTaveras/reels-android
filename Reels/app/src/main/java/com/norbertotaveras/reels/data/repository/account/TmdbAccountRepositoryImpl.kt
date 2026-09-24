/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.account

import com.norbertotaveras.reels.data.local.preferences.TmdbAccountDataStore
import com.norbertotaveras.reels.data.remote.TmdbRemoteDataSource
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.model.account.TmdbAccountStateDataModel
import com.norbertotaveras.reels.domain.repository.account.TmdbAccountRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class TmdbAccountRepositoryImpl @Inject constructor(
    private val remoteDataSource: TmdbRemoteDataSource,
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

    private suspend fun <T> accountResult(block: suspend () -> T): Result<T> {
        return runCatching {
            withContext(ioDispatcher) {
                block()
            }
        }.fold(
            onSuccess = { value -> Result.success(value) },
            onFailure = { error -> Result.failure(error.toTmdbNetworkException()) }
        )
    }

    private companion object {
        const val TMDB_AUTHENTICATE_URL = "https://www.themoviedb.org/authenticate"
    }
}
