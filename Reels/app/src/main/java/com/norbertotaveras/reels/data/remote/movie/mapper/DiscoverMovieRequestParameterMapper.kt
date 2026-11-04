/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.movie.mapper

import com.norbertotaveras.reels.data.remote.movie.DiscoverMovieRequestParameters
import com.norbertotaveras.reels.domain.discover.model.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.discover.model.DiscoverSort

fun DiscoverFilterDataModel.toDiscoverMovieRequestParameters(): DiscoverMovieRequestParameters {
    return DiscoverMovieRequestParameters(
        sortBy = sort.toTmdbParameter(),
        genreId = genreId,
        releaseDateFrom = releaseYearFrom?.let { year -> "$year-01-01" },
        releaseDateTo = releaseYearTo?.let { year -> "$year-12-31" },
        minimumRating = minimumRating
    )
}

private fun DiscoverSort.toTmdbParameter(): String {
    return when (this) {
        DiscoverSort.Popularity -> "popularity.desc"
        DiscoverSort.HighestRated -> "vote_average.desc"
        DiscoverSort.Newest -> "primary_release_date.desc"
        DiscoverSort.Alphabetical -> "original_title.asc"
        DiscoverSort.Revenue -> "revenue.desc"
    }
}
