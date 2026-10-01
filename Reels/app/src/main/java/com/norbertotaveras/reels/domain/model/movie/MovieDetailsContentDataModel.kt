/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.movie

import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel

data class MovieDetailsContentDataModel(
    val movie: MovieDetailsDataModel,
    val cast: List<CastMemberDataModel>,
    val trailers: List<MovieVideoDataModel>,
    val similarMovies: List<MovieDataModel>,
    val isOfflineFavorite: Boolean,
)
