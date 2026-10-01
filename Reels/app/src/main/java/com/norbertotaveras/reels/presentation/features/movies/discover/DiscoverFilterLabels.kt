/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel

internal fun DiscoverUiState.genreLabel(filters: DiscoverFilterDataModel): String? {
    val selectedGenreId = filters.genreId ?: return null
    return genres.firstOrNull { genre -> genre.id == selectedGenreId }?.name
}

internal val DiscoverFilterDataModel.releasePeriodDisplayLabel: String?
    get() {
        val from = releaseYearFrom
        val to = releaseYearTo

        return when {
            from == null && to == null -> null
            from != null && from == to -> from.toString()
            from != null && to == from + 9 && from % 10 == 0 -> "${from}s"
            from != null && to != null -> "$from - $to"
            from != null -> "From $from"
            else -> "Through $to"
        }
    }
