/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.search

import com.norbertotaveras.reels.domain.model.genre.GenreDataModel

data class SearchMetadataDataModel(
    val genres: List<GenreDataModel>,
    val trendingSearches: List<String>,
)
