/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.filmography

import com.norbertotaveras.reels.domain.person.model.FilmographySort

internal val FilmographySort.displayLabel: String
    get() = when (this) {
        FilmographySort.Popular -> "Popular"
        FilmographySort.Newest -> "Newest"
        FilmographySort.HighestRated -> "Highest Rated"
    }
