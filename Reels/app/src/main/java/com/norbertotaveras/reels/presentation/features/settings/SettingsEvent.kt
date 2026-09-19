package com.norbertotaveras.reels.presentation.features.settings

import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion

sealed interface SettingsEvent {
    data object ImageQualityClicked : SettingsEvent
    data class ImageQualitySelected(val imageQuality: ImageQuality) : SettingsEvent
    data object LanguageRegionClicked : SettingsEvent
    data class LanguageRegionSelected(val languageRegion: LanguageRegion) : SettingsEvent
    data object ThemeClicked : SettingsEvent
    data class ThemeSelected(val theme: AppTheme) : SettingsEvent
    data object PreferenceDialogDismissed : SettingsEvent
    data object SavedMoviesClicked : SettingsEvent
    data object ClearImageCacheClicked : SettingsEvent
    data object PrivacyClicked : SettingsEvent
    data object PrivacyNoticeDismissed : SettingsEvent
    data object TmdbAttributionClicked : SettingsEvent
}
