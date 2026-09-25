/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.paging

internal object MovieCachePolicy {
    const val CATEGORY_CACHE_TIMEOUT_MILLIS = 60 * 60 * 1000L
    const val SEARCH_CACHE_TIMEOUT_MILLIS = 24 * 60 * 60 * 1000L
    const val CACHE_RETENTION_MILLIS = 7 * 24 * 60 * 60 * 1000L

    fun isFresh(
        lastUpdatedAt: Long,
        now: Long,
        timeoutMillis: Long,
    ): Boolean {
        return now - lastUpdatedAt < timeoutMillis
    }

    fun retentionCutoff(now: Long): Long {
        return now - CACHE_RETENTION_MILLIS
    }
}
