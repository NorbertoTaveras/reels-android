/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion

sealed interface SettingsEvent {
    data object ScreenResumed : SettingsEvent
    data object ImageQualityClicked : SettingsEvent
    data class ImageQualitySelected(val imageQuality: ImageQuality) : SettingsEvent
    data object LanguageRegionClicked : SettingsEvent
    data class LanguageRegionSelected(val languageRegion: LanguageRegion) : SettingsEvent
    data object ThemeClicked : SettingsEvent
    data class ThemeSelected(val theme: AppTheme) : SettingsEvent
    data object PreferenceDialogDismissed : SettingsEvent
    data object SavedLibraryClicked : SettingsEvent
    data object ClearImageCacheClicked : SettingsEvent
    data object ClearImageCacheConfirmed : SettingsEvent
    data object ClearImageCacheDismissed : SettingsEvent
    data object TmdbAccountClicked : SettingsEvent
    data object TmdbAccountCallbackReceived : SettingsEvent
    data object TmdbAccountFinishClicked : SettingsEvent
    data object TmdbAccountDisconnectClicked : SettingsEvent
    data class TmdbWatchlistSyncChanged(val enabled: Boolean) : SettingsEvent
    data class TmdbRatingsSyncChanged(val enabled: Boolean) : SettingsEvent
    data object PrivacyClicked : SettingsEvent
    data object PrivacyNoticeDismissed : SettingsEvent
    data object TmdbAttributionClicked : SettingsEvent
}
