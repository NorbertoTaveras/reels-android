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
            theme = preferences[THEME_KEY].toEnumOrDefault(AppTheme.System)
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

    private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T {
        return enumValues<T>().firstOrNull { value -> value.name == this } ?: default
    }

    private companion object {
        val IMAGE_QUALITY_KEY = stringPreferencesKey("image_quality")
        val LANGUAGE_REGION_KEY = stringPreferencesKey("language_region")
        val THEME_KEY = stringPreferencesKey("theme")
    }
}
