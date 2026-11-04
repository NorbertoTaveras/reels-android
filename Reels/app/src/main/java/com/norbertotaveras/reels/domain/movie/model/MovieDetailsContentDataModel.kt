/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.movie.model

import com.norbertotaveras.reels.domain.person.model.CastMemberDataModel

data class MovieDetailsContentDataModel(
    val movie: MovieDetailsDataModel,
    val cast: List<CastMemberDataModel>,
    val trailers: List<MovieVideoDataModel>,
    val similarMovies: List<MovieDataModel>,
    val isOfflineContent: Boolean,
    val cachedAt: Long? = null,
)
