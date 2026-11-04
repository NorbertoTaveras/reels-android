/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences.settings

import androidx.datastore.preferences.core.Preferences
import com.norbertotaveras.reels.domain.plans.model.ReleaseReminder
import com.norbertotaveras.reels.domain.settings.model.AppSettings
import com.norbertotaveras.reels.domain.settings.model.AppTheme
import com.norbertotaveras.reels.domain.settings.model.ImageQuality
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion
import com.norbertotaveras.reels.domain.settings.model.PlanPreferences

internal fun Preferences.toAppSettings(): AppSettings {
    return AppSettings(
        imageQuality =
            this[SettingsPreferenceKeys.IMAGE_QUALITY].toEnumOrDefault(ImageQuality.High),
        languageRegion = this[SettingsPreferenceKeys.LANGUAGE_REGION].toLanguageRegion(),
        theme = this[SettingsPreferenceKeys.THEME].toEnumOrDefault(AppTheme.System),
        recentSearches =
            SettingsJsonCodec.decodeRecentSearches(
                value = this[SettingsPreferenceKeys.RECENT_SEARCHES]
            ),
        planPreferences =
            PlanPreferences(
                releaseReminder =
                    this[SettingsPreferenceKeys.RELEASE_REMINDER]
                        .toEnumOrDefault(ReleaseReminder.OneDayBefore)
                        .takeIf {
                            it in
                                listOf(
                                    ReleaseReminder.OnReleaseDay,
                                    ReleaseReminder.OneDayBefore,
                                    ReleaseReminder.OneWeekBefore,
                                )
                        } ?: ReleaseReminder.OneDayBefore,
                movieNightReminderMinutes =
                    (this[SettingsPreferenceKeys.MOVIE_NIGHT_REMINDER] ?: 30)
                        .takeUnless { it == -1 }
                        ?.takeIf { it in listOf(15, 30, 60, 120) },
                liveUpdatesEnabled = this[SettingsPreferenceKeys.LIVE_UPDATES] ?: true,
            ),
        discoverFilters =
            SettingsJsonCodec.decodeDiscoverFilters(
                value = this[SettingsPreferenceKeys.DISCOVER_FILTERS]
            ),
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
