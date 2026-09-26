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

data class SettingsUiState(
    val imageQualityLabel: String = "High quality posters and backdrops",
    val languageRegionLabel: String = "English · United States",
    val themeLabel: String = "Use system setting",
    val selectedImageQuality: ImageQuality = ImageQuality.High,
    val selectedLanguageRegion: LanguageRegion = LanguageRegion.EnglishUnitedStates,
    val selectedTheme: AppTheme = AppTheme.System,
    val savedLibraryLabel: String = "Manage movies and people",
    val clearImageCacheLabel: String = "42 MB · Frees cached poster images",
    val tmdbAccountTitle: String = "Connect TMDB Account",
    val tmdbAccountLabel: String = "Sync watchlist and ratings with TMDB",
    val tmdbAccountName: String = "",
    val isTmdbAccountLoading: Boolean = false,
    val isTmdbAccountConnected: Boolean = false,
    val isTmdbAccountApprovalPending: Boolean = false,
    val appVersionLabel: String = "1.0.0 (build 1)",
    val appConfigurationLabel: String = "Production Debug · Token configured",
    val privacyLabel: String = "How we handle your data",
    val activePreferenceDialog: SettingsPreferenceDialog? = null,
    val isClearImageCacheDialogVisible: Boolean = false,
    val isPrivacyNoticeVisible: Boolean = false,
)

enum class SettingsPreferenceDialog {
    ImageQuality,
    LanguageRegion,
    Theme
}
