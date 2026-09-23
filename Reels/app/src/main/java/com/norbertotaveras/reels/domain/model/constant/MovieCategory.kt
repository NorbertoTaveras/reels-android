/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.constant

enum class MovieCategory(val value: String, val label: String) {
    POPULAR("popular", "Popular"),
    TOP_RATED("top_rated", "Top Rated"),
    UPCOMING("upcoming", "Upcoming"),
    NOW_PLAYING("now_playing", "Now Playing");

    companion object {
        fun fromValue(value: String?): MovieCategory {
            return entries.firstOrNull { category -> category.value == value } ?: POPULAR
        }
    }
}
