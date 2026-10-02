/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.paging

import com.norbertotaveras.reels.data.remote.mapper.movie.toTmdbPathSegment
import com.norbertotaveras.reels.domain.model.constant.MovieCategory

fun MovieCategory.cacheKey(language: String): String {
    return "category:${toTmdbPathSegment()}:language:$language"
}

fun searchCacheKey(
    query: String,
    language: String,
    includeAdult: Boolean,
): String {
    return "search:${query.trim().lowercase()}:language:$language:adult:$includeAdult"
}
