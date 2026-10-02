/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.paging

import com.norbertotaveras.reels.data.remote.mapper.movie.toTmdbPathSegment
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel

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

fun discoverCacheKey(
    filters: DiscoverFilterDataModel,
    language: String,
): String {
    return buildString {
        append("discover:language:", language)
        append(":sort:", filters.sort.name)
        append(":genre:", filters.genreId ?: "all")
        append(":from:", filters.releaseYearFrom ?: "any")
        append(":to:", filters.releaseYearTo ?: "any")
        append(":rating:", filters.minimumRating ?: "any")
    }
}

fun trendingCacheKey(language: String): String {
    return "trending:day:language:$language"
}

fun similarMoviesCacheKey(movieId: Int, language: String): String {
    return "similar:$movieId:language:$language"
}

fun tmdbLibraryCacheKey(
    accountId: Int,
    list: TmdbLibraryList,
    language: String,
): String {
    return "account:$accountId:${list.name.lowercase()}:language:$language"
}
