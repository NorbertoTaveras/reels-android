/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.discover

import com.norbertotaveras.reels.domain.discover.model.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.movie.model.GenreDataModel

internal fun DiscoverUiState.withConfiguration(
    language: String,
    filters: DiscoverFilterDataModel,
): DiscoverUiState {
    return copy(
        language = language,
        filters = filters
    )
}

internal fun DiscoverUiState.withGenresLoading(): DiscoverUiState {
    return copy(
        isLoadingFilters = true,
        filterErrorMessage = null
    )
}

internal fun DiscoverUiState.withGenres(
    genres: List<GenreDataModel>,
    language: String,
): DiscoverUiState {
    if (this.language != language) return this

    return copy(
        genres = genres,
        isLoadingFilters = false,
        filterErrorMessage = null
    )
}

internal fun DiscoverUiState.withGenreError(
    message: String,
    language: String,
): DiscoverUiState {
    if (this.language != language) return this

    return copy(
        isLoadingFilters = false,
        filterErrorMessage = message
    )
}

internal fun DiscoverUiState.withFilterCountLoading(): DiscoverUiState {
    return copy(
        filterResultCount = null,
        isLoadingFilterResultCount = true
    )
}

internal fun DiscoverUiState.withFilterResultCount(
    count: Int?,
    filters: DiscoverFilterDataModel,
    language: String,
): DiscoverUiState {
    if (
        !isFilterSheetVisible ||
        filterSheetFilters != filters ||
        this.language != language
    ) {
        return this
    }

    return copy(
        filterResultCount = count,
        isLoadingFilterResultCount = false
    )
}
