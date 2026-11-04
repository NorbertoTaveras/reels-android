/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.discover

import com.norbertotaveras.reels.domain.discover.model.DiscoverSort

sealed interface DiscoverEvent {
    data object FiltersClicked : DiscoverEvent
    data object FiltersDismissed : DiscoverEvent
    data object ApplyFiltersClicked : DiscoverEvent
    data object ResetDraftFiltersClicked : DiscoverEvent
    data class SortSelected(val sort: DiscoverSort) : DiscoverEvent
    data class GenreSelected(val genreId: Int?) : DiscoverEvent
    data class ReleasePeriodSelected(
        val yearFrom: Int?,
        val yearTo: Int?,
    ) : DiscoverEvent
    data class RatingSelected(val minimumRating: Int?) : DiscoverEvent
    data object ClearFiltersClicked : DiscoverEvent
    data object RetryFiltersClicked : DiscoverEvent
    data class MovieClicked(val movieId: Int) : DiscoverEvent
}
