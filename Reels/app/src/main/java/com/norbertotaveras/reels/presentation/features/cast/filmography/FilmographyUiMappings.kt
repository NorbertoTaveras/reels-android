/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import com.norbertotaveras.reels.domain.model.person.FilmographyContentDataModel

internal fun FilmographyUiState.withContent(
    content: FilmographyContentDataModel,
): FilmographyUiState {
    return copy(
        personName = content.personName ?: personName,
        movies = content.movies,
        isLoading = false,
        errorMessage = null
    )
}
