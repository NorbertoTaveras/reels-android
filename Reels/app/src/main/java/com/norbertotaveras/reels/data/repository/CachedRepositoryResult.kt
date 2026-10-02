/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository

import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal suspend fun <T> cachedTmdbResult(
    dispatcher: CoroutineDispatcher,
    loadRemote: suspend () -> T,
    saveRemote: suspend (T) -> Unit,
    loadCached: suspend () -> T?,
): Result<T> {
    return withContext(dispatcher) {
        val remote = try {
            loadRemote()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            val cached = try {
                loadCached()
            } catch (_: Exception) {
                null
            }

            return@withContext if (cached != null) {
                Result.success(cached)
            } else {
                Result.failure(error.toTmdbNetworkException())
            }
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
