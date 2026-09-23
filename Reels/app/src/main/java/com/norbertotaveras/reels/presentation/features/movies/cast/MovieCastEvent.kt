/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.cast

sealed interface MovieCastEvent {
    data object BackClicked : MovieCastEvent
    data object RetryClicked : MovieCastEvent
    data class CastClicked(val personId: Int) : MovieCastEvent
}
