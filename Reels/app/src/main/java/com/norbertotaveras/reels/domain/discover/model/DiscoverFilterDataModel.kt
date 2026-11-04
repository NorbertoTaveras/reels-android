/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.discover.model

data class DiscoverFilterDataModel(
    val sort: DiscoverSort = DiscoverSort.Popularity,
    val genreId: Int? = null,
    val releaseYearFrom: Int? = null,
    val releaseYearTo: Int? = null,
    val minimumRating: Int? = null,
)

val DiscoverFilterDataModel.hasActiveFilters: Boolean
    get() = this != DiscoverFilterDataModel()
