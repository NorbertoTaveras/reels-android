/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.trailer

data class TrailerCarouselItem(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val source: String? = null,
    val duration: String? = null,
)
