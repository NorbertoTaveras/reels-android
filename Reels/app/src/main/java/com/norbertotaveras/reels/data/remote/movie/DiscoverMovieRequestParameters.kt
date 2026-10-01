/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.movie

data class DiscoverMovieRequestParameters(
    val sortBy: String,
    val genreId: Int?,
    val releaseDateFrom: String?,
    val releaseDateTo: String?,
    val minimumRating: Int?,
)
