/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.constant

enum class MovieCategory(val value: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    UPCOMING("upcoming"),
    NOW_PLAYING("now_playing");

    companion object {
        fun fromValue(value: String?): MovieCategory {
            return entries.firstOrNull { category -> category.value == value } ?: POPULAR
        }
    }
}
