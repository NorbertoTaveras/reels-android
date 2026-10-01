/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import com.norbertotaveras.reels.domain.model.person.FilmographySort
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion

data class FilmographyUiState(
    val personName: String = "Filmography",
    val movies: List<PersonMovieCreditDataModel> = emptyList(),
    val sort: FilmographySort = FilmographySort.Popular,
    val language: String = LanguageRegion.Default.languageCode,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
