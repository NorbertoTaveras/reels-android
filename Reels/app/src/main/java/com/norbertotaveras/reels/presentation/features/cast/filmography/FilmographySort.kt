/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

enum class FilmographySort(
    val label: String,
) {
    Popular(label = "Popular"),
    Newest(label = "Newest"),
    HighestRated(label = "Highest Rated")
}
