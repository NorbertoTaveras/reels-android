/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences

import androidx.datastore.preferences.core.Preferences
import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion

internal fun Preferences.toAppSettings(): AppSettings {
    return AppSettings(
        imageQuality = this[SettingsPreferenceKeys.IMAGE_QUALITY]
            .toEnumOrDefault(ImageQuality.High),
        languageRegion = this[SettingsPreferenceKeys.LANGUAGE_REGION].toLanguageRegion(),
        theme = this[SettingsPreferenceKeys.THEME].toEnumOrDefault(AppTheme.System),
        recentSearches = SettingsJsonCodec.decodeRecentSearches(
            value = this[SettingsPreferenceKeys.RECENT_SEARCHES]
        ),
        discoverFilters = SettingsJsonCodec.decodeDiscoverFilters(
            value = this[SettingsPreferenceKeys.DISCOVER_FILTERS]
        )
    )
}

private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T {
    return enumValues<T>().firstOrNull { value -> value.name == this } ?: default
}

private fun String?.toLanguageRegion(): LanguageRegion {
    return when (this) {
        LEGACY_SPANISH_REGION -> LanguageRegion.SpanishSpain
        else -> toEnumOrDefault(LanguageRegion.Default)
    }
}

private const val LEGACY_SPANISH_REGION = "SpanishUnitedStates"
