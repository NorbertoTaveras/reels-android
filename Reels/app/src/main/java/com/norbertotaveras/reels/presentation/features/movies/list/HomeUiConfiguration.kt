/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.list

import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.presentation.ui.extensions.displayLabel

internal const val HERO_MOVIE_LIMIT = 3
internal const val HERO_AUTO_SCROLL_DELAY_MS = 2_800L
internal const val HERO_CROSSFADE_DURATION_MS = 650
internal const val HOME_SECTION_LIMIT = 10
internal const val SECTION_PLACEHOLDER_COUNT = 4

internal val HOME_LOADING_SECTION_TITLES = listOf(
    MovieCategory.POPULAR.displayLabel,
    MovieCategory.NOW_PLAYING.displayLabel,
    MovieCategory.UPCOMING.displayLabel,
    MovieCategory.TOP_RATED.displayLabel
)
