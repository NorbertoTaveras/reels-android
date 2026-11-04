/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import com.norbertotaveras.reels.domain.movie.model.MovieCategory

val MovieCategory.displayLabel: String
    get() = when (this) {
        MovieCategory.POPULAR -> "Popular"
        MovieCategory.TOP_RATED -> "Top Rated"
        MovieCategory.UPCOMING -> "Upcoming"
        MovieCategory.NOW_PLAYING -> "Now Playing"
    }

val MovieCategory.routeValue: String
    get() = name.lowercase()

fun String?.toMovieCategory(): MovieCategory {
    return when (this?.lowercase()) {
        "top_rated" -> MovieCategory.TOP_RATED
        "upcoming" -> MovieCategory.UPCOMING
        "now_playing" -> MovieCategory.NOW_PLAYING
        else -> MovieCategory.POPULAR
    }
}
