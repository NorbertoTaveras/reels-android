/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import com.norbertotaveras.reels.domain.model.constant.MovieCategory

val MovieCategory.displayLabel: String
    get() = when (this) {
        MovieCategory.POPULAR -> "Popular"
        MovieCategory.TOP_RATED -> "Top Rated"
        MovieCategory.UPCOMING -> "Upcoming"
        MovieCategory.NOW_PLAYING -> "Now Playing"
    }
