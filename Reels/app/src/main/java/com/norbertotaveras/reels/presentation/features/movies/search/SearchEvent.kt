/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

sealed interface SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent
    data object ClearQueryClicked : SearchEvent
    data object SearchSubmitted : SearchEvent
    data class RecentSearchClicked(val query: String) : SearchEvent
    data object ClearRecentSearchesClicked : SearchEvent
    data class MovieClicked(val movieId: Int) : SearchEvent
}
