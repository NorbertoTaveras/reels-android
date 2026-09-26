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

data class DiscoverUiState(
    val filters: DiscoverFilterDataModel = DiscoverFilterDataModel(),
    val language: String = "en-US",
    val genres: List<GenreDataModel> = emptyList(),
    val isLoadingFilters: Boolean = true,
    val isFilterSheetVisible: Boolean = false,
    val filterErrorMessage: String? = null,
)

val DiscoverUiState.hasActiveFilters: Boolean
    get() = filters.hasActiveFilters
