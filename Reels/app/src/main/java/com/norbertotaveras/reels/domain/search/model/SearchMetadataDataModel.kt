/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.search.model

import com.norbertotaveras.reels.domain.movie.model.GenreDataModel

data class SearchMetadataDataModel(
    val genres: List<GenreDataModel>,
    val trendingSearches: List<String>,
)
