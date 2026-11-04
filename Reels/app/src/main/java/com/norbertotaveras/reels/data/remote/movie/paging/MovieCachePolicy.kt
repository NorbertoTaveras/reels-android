/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.movie.paging

internal object MovieCachePolicy {
    const val CACHE_RETENTION_MILLIS = 7 * 24 * 60 * 60 * 1000L

    fun retentionCutoff(now: Long): Long {
        return now - CACHE_RETENTION_MILLIS
    }
}
