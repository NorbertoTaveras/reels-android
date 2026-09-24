/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

sealed interface FilmographyEvent {
    data object BackClicked : FilmographyEvent
    data object RetryClicked : FilmographyEvent
    data class SortSelected(val sort: FilmographySort) : FilmographyEvent
    data class MovieClicked(val movieId: Int) : FilmographyEvent
}
