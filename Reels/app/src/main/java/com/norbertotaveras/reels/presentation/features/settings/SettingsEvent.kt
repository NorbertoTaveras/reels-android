package com.norbertotaveras.reels.presentation.features.settings

sealed interface SettingsEvent {
    data object ImageQualityClicked : SettingsEvent
    data object LanguageRegionClicked : SettingsEvent
    data object ThemeClicked : SettingsEvent
    data object SavedMoviesClicked : SettingsEvent
    data object ClearImageCacheClicked : SettingsEvent
    data object PrivacyClicked : SettingsEvent
    data object PrivacyNoticeDismissed : SettingsEvent
    data object TmdbAttributionClicked : SettingsEvent
}
