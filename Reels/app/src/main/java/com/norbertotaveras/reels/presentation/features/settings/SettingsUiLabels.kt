/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import com.norbertotaveras.reels.BuildConfig
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion

internal fun buildAppVersionLabel(): String {
    return "${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})"
}

internal fun buildAppConfigurationLabel(): String {
    val flavorLabel = BuildConfig.FLAVOR.toDisplayLabel()
    val buildTypeLabel = BuildConfig.BUILD_TYPE.toDisplayLabel()
    val tokenStatusLabel = if (BuildConfig.TMDB_READ_TOKEN.isBlank()) {
        "Token missing"
    } else {
        "Token configured"
    }

    return "$flavorLabel $buildTypeLabel · $tokenStatusLabel"
}

internal fun buildSavedLibraryLabel(
    movieCount: Int,
    personCount: Int,
): String {
    if (movieCount == 0 && personCount == 0) {
        return "Manage movies and people"
    }

    val movieLabel = "$movieCount ${if (movieCount == 1) "movie" else "movies"}"
    val personLabel = "$personCount ${if (personCount == 1) "person" else "people"}"
    return "$movieLabel · $personLabel"
}

internal val LanguageRegion.languageName: String
    get() = when (this) {
        LanguageRegion.EnglishUnitedStates -> "English"
        LanguageRegion.SpanishSpain -> "Español"
        LanguageRegion.FrenchFrance -> "Français"
        LanguageRegion.JapaneseJapan -> "日本語"
    }

internal val LanguageRegion.regionName: String
    get() = when (this) {
        LanguageRegion.EnglishUnitedStates -> "United States"
        LanguageRegion.SpanishSpain -> "España"
        LanguageRegion.FrenchFrance -> "France"
        LanguageRegion.JapaneseJapan -> "日本"
    }

private fun String.toDisplayLabel(): String {
    return replaceFirstChar { character ->
        if (character.isLowerCase()) {
            character.titlecase()
        } else {
            character.toString()
        }
    }
}
