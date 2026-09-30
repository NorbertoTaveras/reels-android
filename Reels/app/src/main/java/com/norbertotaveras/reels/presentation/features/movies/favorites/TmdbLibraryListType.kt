/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

internal enum class TmdbLibraryListType {
    Watchlist,
    Rated
}

internal val TmdbLibraryListType.emptyTab: LibraryTab
    get() = when (this) {
        TmdbLibraryListType.Watchlist -> LibraryTab.Tmdb
        TmdbLibraryListType.Rated -> LibraryTab.Rated
    }
