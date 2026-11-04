/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.category

import com.norbertotaveras.reels.domain.movie.model.MovieCategory
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion

data class MovieCategoryUiState(
    val category: MovieCategory = MovieCategory.POPULAR,
    val language: String = LanguageRegion.Default.languageCode,
)
