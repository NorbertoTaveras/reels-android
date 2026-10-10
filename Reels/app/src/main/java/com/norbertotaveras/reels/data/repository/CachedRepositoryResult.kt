/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository

import com.norbertotaveras.reels.data.remote.error.NoNetworkConnectionException
import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import com.norbertotaveras.reels.domain.network.NetworkMonitor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal suspend fun <T> cachedTmdbResult(
    dispatcher: CoroutineDispatcher,
    networkMonitor: NetworkMonitor,
    loadRemote: suspend () -> T,
    saveRemote: suspend (T) -> Unit,
    loadCached: suspend () -> T?,
): Result<T> {
    return withContext(dispatcher) {
        if (!networkMonitor.isOnline()) {
            return@withContext cachedResultOrFailure(
                loadCached = loadCached,
                error = NoNetworkConnectionException()
            )
        }

        val remote = try {
            loadRemote()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            return@withContext cachedResultOrFailure(loadCached = loadCached, error = error)
        }

        try {
            saveRemote(remote)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            // A cache write must not hide a successful network response.
        }

        Result.success(remote)
    }
}

private suspend fun <T> cachedResultOrFailure(
    loadCached: suspend () -> T?,
    error: Exception,
): Result<T> {
    val cached = try {
        loadCached()
    } catch (_: Exception) {
        null
    }

    return cached?.let(Result.Companion::success)
        ?: Result.failure(error.toTmdbNetworkException())
}
