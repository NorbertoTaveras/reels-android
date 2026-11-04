/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.norbertotaveras.reels.domain.discover.model.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.settings.model.AppSettings
import com.norbertotaveras.reels.domain.settings.model.AppTheme
import com.norbertotaveras.reels.domain.settings.model.ImageQuality
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion
import com.norbertotaveras.reels.domain.settings.model.PlanPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore
@Inject
constructor(@param:ApplicationContext private val context: Context) {
    val settings: Flow<AppSettings> =
        context.settingsDataStore.data.map { preferences -> preferences.toAppSettings() }

    suspend fun updateImageQuality(imageQuality: ImageQuality) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsPreferenceKeys.IMAGE_QUALITY] = imageQuality.name
        }
    }

    suspend fun updateLanguageRegion(languageRegion: LanguageRegion) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsPreferenceKeys.LANGUAGE_REGION] = languageRegion.name
        }
    }

    suspend fun updateTheme(theme: AppTheme) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsPreferenceKeys.THEME] = theme.name
        }
    }

    suspend fun updatePlanPreferences(value: PlanPreferences) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsPreferenceKeys.RELEASE_REMINDER] = value.releaseReminder.name
            preferences[SettingsPreferenceKeys.MOVIE_NIGHT_REMINDER] =
                value.movieNightReminderMinutes ?: -1
            preferences[SettingsPreferenceKeys.LIVE_UPDATES] = value.liveUpdatesEnabled
        }
    }

    suspend fun updateRecentSearches(recentSearches: List<String>) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsPreferenceKeys.RECENT_SEARCHES] =
                SettingsJsonCodec.encodeRecentSearches(recentSearches = recentSearches)
        }
    }

    suspend fun updateDiscoverFilters(filters: DiscoverFilterDataModel) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsPreferenceKeys.DISCOVER_FILTERS] =
                SettingsJsonCodec.encodeDiscoverFilters(filters = filters)
        }
    }
}
