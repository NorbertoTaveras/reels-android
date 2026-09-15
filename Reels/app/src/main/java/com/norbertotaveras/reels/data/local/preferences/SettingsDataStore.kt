package com.norbertotaveras.reels.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            imageQuality = preferences[IMAGE_QUALITY_KEY].toEnumOrDefault(ImageQuality.High),
            languageRegion = preferences[LANGUAGE_REGION_KEY].toEnumOrDefault(
                LanguageRegion.EnglishUnitedStates
            ),
            theme = preferences[THEME_KEY].toEnumOrDefault(AppTheme.System),
            recentSearches = preferences[RECENT_SEARCHES_KEY].toRecentSearches()
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

    private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T {
        return enumValues<T>().firstOrNull { value -> value.name == this } ?: default
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

    private companion object {
        val IMAGE_QUALITY_KEY = stringPreferencesKey("image_quality")
        val LANGUAGE_REGION_KEY = stringPreferencesKey("language_region")
        val THEME_KEY = stringPreferencesKey("theme")
        val RECENT_SEARCHES_KEY = stringPreferencesKey("recent_searches")
    }
}
