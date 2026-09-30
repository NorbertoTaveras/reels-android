/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.movie

data class PosterCarouselItem(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val releaseYear: String? = null,
    val rating: String? = null,
)
