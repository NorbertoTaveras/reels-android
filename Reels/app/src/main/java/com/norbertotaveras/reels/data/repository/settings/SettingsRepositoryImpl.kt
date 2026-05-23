package com.norbertotaveras.reels.data.repository.settings

import com.norbertotaveras.reels.data.local.preferences.SettingsDataStore
import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
) : SettingsRepository {
    override fun observeSettings(): Flow<AppSettings> {
        return settingsDataStore.settings
    }

    override suspend fun updateImageQuality(imageQuality: ImageQuality) {
        settingsDataStore.updateImageQuality(imageQuality = imageQuality)
    }

    override suspend fun updateLanguageRegion(languageRegion: LanguageRegion) {
        settingsDataStore.updateLanguageRegion(languageRegion = languageRegion)
    }

    override suspend fun updateTheme(theme: AppTheme) {
        settingsDataStore.updateTheme(theme = theme)
    }
}
