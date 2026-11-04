/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.filmography

import com.norbertotaveras.reels.domain.person.model.FilmographySort
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditDataModel

data class FilmographyUiState(
    val personName: String = "Filmography",
    val movies: List<PersonMovieCreditDataModel> = emptyList(),
    val sort: FilmographySort = FilmographySort.Popular,
    val isLoading: Boolean = true,
    val isOnline: Boolean = true,
    val errorMessage: String? = null,
)
