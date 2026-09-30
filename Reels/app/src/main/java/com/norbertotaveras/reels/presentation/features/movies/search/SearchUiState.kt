/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import com.norbertotaveras.reels.domain.model.settings.LanguageRegion

data class SearchUiState(
    val query: String = "",
    val language: String = LanguageRegion.Default.languageCode,
    val recentSearches: List<String> = emptyList(),
    val trendingSearches: List<String> = emptyList(),
    val genreNamesById: Map<Int, String> = emptyMap()
)
