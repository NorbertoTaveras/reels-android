/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.paging

import androidx.paging.PagingConfig

internal fun tmdbPagingConfig(): PagingConfig {
    return PagingConfig(
        pageSize = TMDB_PAGE_SIZE,
        initialLoadSize = TMDB_PAGE_SIZE,
        prefetchDistance = TMDB_PREFETCH_DISTANCE,
        enablePlaceholders = false
    )
}

private const val TMDB_PAGE_SIZE = 20
private const val TMDB_PREFETCH_DISTANCE = 5
