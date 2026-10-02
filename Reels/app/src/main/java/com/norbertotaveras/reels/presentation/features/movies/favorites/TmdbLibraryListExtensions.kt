/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList

internal val TmdbLibraryList.emptyTab: LibraryTab
    get() = when (this) {
        TmdbLibraryList.Watchlist -> LibraryTab.Tmdb
        TmdbLibraryList.Rated -> LibraryTab.Rated
    }
