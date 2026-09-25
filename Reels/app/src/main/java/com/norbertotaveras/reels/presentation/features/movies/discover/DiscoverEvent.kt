/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import com.norbertotaveras.reels.domain.model.discover.DiscoverSort

sealed interface DiscoverEvent {
    data class SortSelected(val sort: DiscoverSort) : DiscoverEvent
    data class GenreSelected(val genreId: Int?, val genreName: String?) : DiscoverEvent
    data class YearSelected(val year: Int?) : DiscoverEvent
    data class RatingSelected(val minimumRating: Int?) : DiscoverEvent
    data object ClearFiltersClicked : DiscoverEvent
    data object RetryFiltersClicked : DiscoverEvent
    data class MovieClicked(val movieId: Int) : DiscoverEvent
}
