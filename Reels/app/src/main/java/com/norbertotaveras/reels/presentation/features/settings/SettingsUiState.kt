package com.norbertotaveras.reels.presentation.features.settings

import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion

data class SettingsUiState(
    val imageQualityLabel: String = "High quality posters and backdrops",
    val languageRegionLabel: String = "English · United States",
    val themeLabel: String = "Use system setting",
    val selectedImageQuality: ImageQuality = ImageQuality.High,
    val selectedLanguageRegion: LanguageRegion = LanguageRegion.EnglishUnitedStates,
    val selectedTheme: AppTheme = AppTheme.System,
    val savedMoviesLabel: String = "Manage movies saved on this device",
    val clearImageCacheLabel: String = "Free up locally cached poster images",
    val appVersionLabel: String = "1.0.0",
    val privacyLabel: String = "Local favorites stay on this device",
    val activePreferenceDialog: SettingsPreferenceDialog? = null,
    val isPrivacyNoticeVisible: Boolean = false,
)

enum class SettingsPreferenceDialog {
    ImageQuality,
    LanguageRegion,
    Theme
}
