/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.components.trailer.TrailerCarouselItem

internal val previewCast = listOf(
    CastMemberDataModel(
        id = 1,
        name = "Timothée Chalamet",
        characterName = "Paul Atreides",
        profilePath = null
    ),
    CastMemberDataModel(
        id = 2,
        name = "Zendaya",
        characterName = "Chani",
        profilePath = null
    ),
    CastMemberDataModel(
        id = 3,
        name = "Rebecca Ferguson",
        characterName = "Lady Jessica",
        profilePath = null
    ),
    CastMemberDataModel(
        id = 4,
        name = "Josh Brolin",
        characterName = "Gurney Halleck",
        profilePath = null
    )
)

internal val previewTrailers = listOf(
    TrailerCarouselItem(
        id = "official",
        title = "Official Trailer",
        thumbnailUrl = null,
        source = "YouTube",
        duration = "3:12"
    ),
    TrailerCarouselItem(
        id = "final",
        title = "Final Trailer",
        thumbnailUrl = null,
        source = "YouTube",
        duration = "2:45"
    )
)

internal val previewSimilarMovies = listOf(
    PosterCarouselItem(
        id = 1,
        title = "Oppenheimer",
        posterUrl = null
    ),
    PosterCarouselItem(
        id = 2,
        title = "Poor Things",
        posterUrl = null
    ),
    PosterCarouselItem(
        id = 3,
        title = "Killers of the Flower Moon",
        posterUrl = null
    )
)
