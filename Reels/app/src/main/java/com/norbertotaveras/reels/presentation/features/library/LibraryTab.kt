/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.library

enum class LibraryTab(
    val title: String,
) {
    Movies(title = "Movies"),
    People(title = "People"),
    Tmdb(title = "Watchlist"),
    Rated(title = "Rated")
}
