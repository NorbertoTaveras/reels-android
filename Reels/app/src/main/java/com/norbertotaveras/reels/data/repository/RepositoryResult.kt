/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal suspend fun <T> repositoryResult(
    dispatcher: CoroutineDispatcher,
    transformError: suspend (Exception) -> Throwable = { error -> error },
    block: suspend () -> T,
): Result<T> {
    return try {
        Result.success(
            withContext(dispatcher) {
                block()
            }
        )
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Result.failure(transformError(error))
    }
}
