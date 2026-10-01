/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import com.norbertotaveras.reels.domain.model.search.SearchMetadataDataModel
import com.norbertotaveras.reels.domain.model.settings.AppSettings

internal fun SearchUiState.withSettings(settings: AppSettings): SearchUiState {
    return copy(
        language = settings.languageRegion.languageCode,
        recentSearches = settings.recentSearches
    )
}

internal fun SearchUiState.withMetadata(
    metadata: SearchMetadataDataModel,
): SearchUiState {
    return copy(
        trendingSearches = metadata.trendingSearches,
        genreNamesById = metadata.genres.associate { genre ->
            genre.id to genre.name
        }
    )
}
