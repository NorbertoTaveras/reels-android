/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

data class SearchUiState(
    val query: String = "",
    val language: String = "en-US",
    val recentSearches: List<String> = emptyList(),
    val genreNamesById: Map<Int, String> = emptyMap()
)
