/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.discover

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.discover.model.DiscoverSort
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion
import com.norbertotaveras.reels.presentation.ui.theme.ReelsGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightGenreChipContainer
import com.norbertotaveras.reels.presentation.ui.theme.isReelsLightTheme
import java.text.NumberFormat
import java.util.Calendar

internal val DISCOVER_RATINGS = listOf(null, 5, 6, 7, 8)
internal val DISCOVER_SORTS = listOf(
    DiscoverSort.Popularity,
    DiscoverSort.HighestRated,
    DiscoverSort.Newest,
    DiscoverSort.Alphabetical
)
internal const val DISCOVER_PLACEHOLDER_COUNT = 12
internal const val DISCOVER_APPEND_PLACEHOLDER_COUNT = 3
internal const val DISCOVER_GENRE_COLUMNS = 4
internal const val DISCOVER_FILTER_COUNT_DEBOUNCE_MILLIS = 250L
internal val DISCOVER_FILTER_SKELETON_WIDTHS = listOf(
    72.dp,
    90.dp,
    82.dp,
    76.dp,
    68.dp,
    78.dp,
    66.dp,
    88.dp,
    72.dp,
    80.dp
)

internal val DiscoverSort.displayLabel: String
    get() = when (this) {
        DiscoverSort.Popularity -> "Popularity"
        DiscoverSort.HighestRated -> "Rating"
        DiscoverSort.Newest -> "Release Date"
        DiscoverSort.Alphabetical -> "A – Z"
        DiscoverSort.Revenue -> "Box Office"
    }

internal val DiscoverSort.descriptionLabel: String
    get() = when (this) {
        DiscoverSort.Popularity -> "Most watched first"
        DiscoverSort.HighestRated -> "Highest rated first"
        DiscoverSort.Newest -> "Newest releases first"
        DiscoverSort.Alphabetical -> "Alphabetical order"
        DiscoverSort.Revenue -> "Highest box office first"
    }

internal fun Int.toMovieCountLabel(capitalize: Boolean = false): String {
    val formattedCount = NumberFormat.getIntegerInstance().format(this)
    return when {
        this == 1 && capitalize -> "1 Movie"
        this == 1 -> "1 movie"
        capitalize -> "$formattedCount Movies"
        else -> "$formattedCount movies"
    }
}

internal fun String.toDiscoverLanguageLabel(): String {
    return when (this) {
        LanguageRegion.Default.languageCode -> "English · All regions"
        "en-GB" -> "English · United Kingdom"
        "es-ES" -> "Spanish · Spain"
        "fr-FR" -> "French · France"
        else -> this
    }
}

internal fun discoverReleasePeriods(): List<DiscoverReleasePeriod> {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val latestCompletedDecade = (currentYear / 10 - 1) * 10

    return buildList {
        repeat(4) { offset ->
            val year = currentYear - offset
            add(
                DiscoverReleasePeriod(
                    label = year.toString(),
                    yearFrom = year,
                    yearTo = year
                )
            )
        }
        repeat(2) { offset ->
            val decadeStart = latestCompletedDecade - (offset * 10)
            add(
                DiscoverReleasePeriod(
                    label = "${decadeStart}s",
                    yearFrom = decadeStart,
                    yearTo = decadeStart + 9
                )
            )
        }
    }
}

@Composable
internal fun discoverFilterControlColor(): Color {
    return if (isReelsLightTheme()) {
        ReelsLightGenreChipContainer
    } else {
        ReelsGenreChipContainer.copy(alpha = 0.58f)
    }
}

internal val DiscoverUiState.activeFilterCount: Int
    get() {
        var count = 0
        if (filters.genreId != null) count += 1
        if (filters.releaseYearFrom != null || filters.releaseYearTo != null) count += 1
        if (filters.minimumRating != null) count += 1
        if (filters.sort != DiscoverSort.Popularity) count += 1
        return count
    }
