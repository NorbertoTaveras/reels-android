/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.movie.model

import com.norbertotaveras.reels.domain.person.model.CastMemberDataModel

sealed interface MovieDetailsContentUpdate {
    data class Main(
        val movie: MovieDetailsDataModel,
        val cachedAt: Long?,
        val isOfflineContent: Boolean = cachedAt != null,
    ) : MovieDetailsContentUpdate

    data class Cast(val members: List<CastMemberDataModel>) : MovieDetailsContentUpdate
    data class Trailers(val videos: List<MovieVideoDataModel>) : MovieDetailsContentUpdate
    data class Similar(val movies: List<MovieDataModel>) : MovieDetailsContentUpdate
    data class Failure(val error: Throwable) : MovieDetailsContentUpdate
}
