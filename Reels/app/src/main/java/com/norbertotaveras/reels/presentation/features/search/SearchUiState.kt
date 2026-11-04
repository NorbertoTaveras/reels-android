/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.search

import com.norbertotaveras.reels.domain.settings.model.LanguageRegion

data class SearchUiState(
    val query: String = "",
    val language: String = LanguageRegion.Default.languageCode,
    val isOnline: Boolean = true,
    val cachedAt: Long? = null,
    val recentSearches: List<String> = emptyList(),
    val trendingSearches: List<String> = emptyList(),
    val genreNamesById: Map<Int, String> = emptyMap()
)
