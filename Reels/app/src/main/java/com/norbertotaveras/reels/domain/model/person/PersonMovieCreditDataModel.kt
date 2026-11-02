/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.person

data class PersonMovieCreditDataModel(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val character: String?,
    val voteAverage: Double,
    val popularity: Double
)
