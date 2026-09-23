/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.cast

sealed interface MovieCastEffect {
    data object NavigateBack : MovieCastEffect
    data class NavigateToCastDetails(val personId: Int) : MovieCastEffect
}
