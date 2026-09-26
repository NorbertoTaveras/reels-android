/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.discover

data class DiscoverFilterDataModel(
    val sort: DiscoverSort = DiscoverSort.Popularity,
    val genreId: Int? = null,
    val genreName: String? = null,
    val year: Int? = null,
    val minimumRating: Int? = null,
)

val DiscoverFilterDataModel.hasActiveFilters: Boolean
    get() = this != DiscoverFilterDataModel()

enum class DiscoverSort(
    val label: String,
    val apiValue: String,
) {
    Popularity(
        label = "Popular",
        apiValue = "popularity.desc"
    ),
    HighestRated(
        label = "Highest Rated",
        apiValue = "vote_average.desc"
    ),
    Newest(
        label = "Newest",
        apiValue = "primary_release_date.desc"
    ),
    Alphabetical(
        label = "A - Z",
        apiValue = "original_title.asc"
    ),
    Revenue(
        label = "Box Office",
        apiValue = "revenue.desc"
    )
}
