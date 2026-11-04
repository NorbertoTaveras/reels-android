/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.movie.mapper

import com.norbertotaveras.reels.domain.movie.model.MovieCategory

fun MovieCategory.toTmdbPathSegment(): String {
    return when (this) {
        MovieCategory.POPULAR -> "popular"
        MovieCategory.TOP_RATED -> "top_rated"
        MovieCategory.UPCOMING -> "upcoming"
        MovieCategory.NOW_PLAYING -> "now_playing"
    }
}
