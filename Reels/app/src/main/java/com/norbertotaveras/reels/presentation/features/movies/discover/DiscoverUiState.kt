/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.discover.hasActiveFilters
import com.norbertotaveras.reels.domain.model.genre.GenreDataModel
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion

data class DiscoverUiState(
    val filters: DiscoverFilterDataModel = DiscoverFilterDataModel(),
    val draftFilters: DiscoverFilterDataModel? = null,
    val language: String = LanguageRegion.Default.languageCode,
    val isOnline: Boolean = true,
    val genres: List<GenreDataModel> = emptyList(),
    val isLoadingFilters: Boolean = true,
    val filterResultCount: Int? = null,
    val isLoadingFilterResultCount: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
    val filterErrorMessage: String? = null,
)

val DiscoverUiState.hasActiveFilters: Boolean
    get() = filters.hasActiveFilters

val DiscoverUiState.filterSheetFilters: DiscoverFilterDataModel
    get() = draftFilters ?: filters
