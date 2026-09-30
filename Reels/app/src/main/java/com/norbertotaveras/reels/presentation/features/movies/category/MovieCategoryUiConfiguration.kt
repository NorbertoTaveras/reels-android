/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.category

import com.norbertotaveras.reels.domain.model.constant.MovieCategory

internal val MovieCategory.supportingText: String
    get() = when (this) {
        MovieCategory.POPULAR -> "What everyone is watching right now"
        MovieCategory.NOW_PLAYING -> "Movies playing in theaters now"
        MovieCategory.UPCOMING -> "Movies arriving in theaters soon"
        MovieCategory.TOP_RATED -> "The highest-rated movies on TMDB"
    }

internal const val CATEGORY_PLACEHOLDER_COUNT = 12
internal const val CATEGORY_APPEND_PLACEHOLDER_COUNT = 3
