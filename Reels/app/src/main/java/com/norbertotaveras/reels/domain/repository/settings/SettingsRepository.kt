package com.norbertotaveras.reels.domain.repository.settings

import com.norbertotaveras.reels.domain.model.settings.AppSettings
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun updateImageQuality(imageQuality: ImageQuality)
    suspend fun updateLanguageRegion(languageRegion: LanguageRegion)
    suspend fun updateTheme(theme: AppTheme)
}
