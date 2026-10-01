/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository

import com.norbertotaveras.reels.data.remote.error.toTmdbNetworkException
import kotlinx.coroutines.CoroutineDispatcher

internal suspend fun <T> tmdbResult(
    dispatcher: CoroutineDispatcher,
    block: suspend () -> T,
): Result<T> {
    return repositoryResult(
        dispatcher = dispatcher,
        transformError = { error -> error.toTmdbNetworkException() },
        block = block
    )
}
