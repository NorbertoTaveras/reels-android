/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.discover.DiscoverSort
import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            imageQuality = preferences[IMAGE_QUALITY_KEY].toEnumOrDefault(ImageQuality.High),
            languageRegion = preferences[LANGUAGE_REGION_KEY].toLanguageRegion(),
            theme = preferences[THEME_KEY].toEnumOrDefault(AppTheme.System),
            recentSearches = preferences[RECENT_SEARCHES_KEY].toRecentSearches(),
            discoverFilters = preferences[DISCOVER_FILTERS_KEY].toDiscoverFilters()
        )
    }

    suspend fun updateImageQuality(imageQuality: ImageQuality) {
        context.settingsDataStore.edit { preferences ->
            preferences[IMAGE_QUALITY_KEY] = imageQuality.name
        }
    }

    suspend fun updateLanguageRegion(languageRegion: LanguageRegion) {
        context.settingsDataStore.edit { preferences ->
            preferences[LANGUAGE_REGION_KEY] = languageRegion.name
        }
    }

    suspend fun updateTheme(theme: AppTheme) {
        context.settingsDataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    suspend fun updateRecentSearches(recentSearches: List<String>) {
        context.settingsDataStore.edit { preferences ->
            preferences[RECENT_SEARCHES_KEY] = recentSearches.toJsonArrayString()
        }
    }

    suspend fun updateDiscoverFilters(filters: DiscoverFilterDataModel) {
        context.settingsDataStore.edit { preferences ->
            preferences[DISCOVER_FILTERS_KEY] = filters.toJsonObjectString()
        }
    }

    private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T {
        return enumValues<T>().firstOrNull { value -> value.name == this } ?: default
    }

    private fun String?.toLanguageRegion(): LanguageRegion {
        return when (this) {
            LEGACY_SPANISH_REGION -> LanguageRegion.SpanishSpain
            else -> toEnumOrDefault(LanguageRegion.EnglishUnitedStates)
        }
    }

    private fun String?.toRecentSearches(): List<String> {
        if (this.isNullOrBlank()) return emptyList()

        return try {
            val jsonArray = JSONArray(this)
            buildList {
                repeat(jsonArray.length()) { index ->
                    jsonArray.optString(index)
                        .trim()
                        .takeIf { query -> query.isNotBlank() }
                        ?.let(::add)
                }
            }
        } catch (_: JSONException) {
            emptyList()
        }
    }

    private fun List<String>.toJsonArrayString(): String {
        val jsonArray = JSONArray()
        forEach { query ->
            jsonArray.put(query)
        }
        return jsonArray.toString()
    }

    private fun String?.toDiscoverFilters(): DiscoverFilterDataModel {
        if (this.isNullOrBlank()) return DiscoverFilterDataModel()

        return try {
            val jsonObject = JSONObject(this)
            DiscoverFilterDataModel(
                sort = jsonObject.optString(DISCOVER_SORT)
                    .toEnumOrDefault(DiscoverSort.Popularity),
                genreId = jsonObject.optionalInt(DISCOVER_GENRE_ID),
                genreName = jsonObject.optionalString(DISCOVER_GENRE_NAME),
                year = jsonObject.optionalInt(DISCOVER_YEAR),
                minimumRating = jsonObject.optionalInt(DISCOVER_MINIMUM_RATING)
            )
        } catch (_: JSONException) {
            DiscoverFilterDataModel()
        }
    }

    private fun DiscoverFilterDataModel.toJsonObjectString(): String {
        return JSONObject()
            .put(DISCOVER_SORT, sort.name)
            .put(DISCOVER_GENRE_ID, genreId)
            .put(DISCOVER_GENRE_NAME, genreName)
            .put(DISCOVER_YEAR, year)
            .put(DISCOVER_MINIMUM_RATING, minimumRating)
            .toString()
    }

    private fun JSONObject.optionalInt(name: String): Int? {
        return if (isNull(name)) null else optInt(name)
    }

    private fun JSONObject.optionalString(name: String): String? {
        return if (isNull(name)) null else optString(name).takeIf { value -> value.isNotBlank() }
    }

    private companion object {
        const val LEGACY_SPANISH_REGION = "SpanishUnitedStates"
        val IMAGE_QUALITY_KEY = stringPreferencesKey("image_quality")
        val LANGUAGE_REGION_KEY = stringPreferencesKey("language_region")
        val THEME_KEY = stringPreferencesKey("theme")
        val RECENT_SEARCHES_KEY = stringPreferencesKey("recent_searches")
        val DISCOVER_FILTERS_KEY = stringPreferencesKey("discover_filters")

        const val DISCOVER_SORT = "sort"
        const val DISCOVER_GENRE_ID = "genreId"
        const val DISCOVER_GENRE_NAME = "genreName"
        const val DISCOVER_YEAR = "year"
        const val DISCOVER_MINIMUM_RATING = "minimumRating"
    }
}
