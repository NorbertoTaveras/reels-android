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
import com.norbertotaveras.reels.domain.model.settings.PlanPreferences

data class SettingsUiState(
    val planPreferences: PlanPreferences = PlanPreferences(),
    val canPostNotifications: Boolean = false,
    val canScheduleExactAlarms: Boolean = false,
    val supportsLiveUpdates: Boolean = false,
    val canPromoteLiveUpdates: Boolean = false,
    val imageQualityLabel: String = "High quality",
    val languageRegionLabel: String = "English · United States",
    val themeLabel: String = "System default",
    val selectedImageQuality: ImageQuality = ImageQuality.High,
    val selectedLanguageRegion: LanguageRegion = LanguageRegion.EnglishUnitedStates,
    val selectedTheme: AppTheme = AppTheme.System,
    val savedLibraryLabel: String = "Manage movies and people",
    val imageCacheSizeLabel: String = "0 B",
    val clearImageCacheLabel: String = "0 B · Frees cached poster images",
    val tmdbAccountName: String = "",
    val tmdbAccountUsername: String = "",
    val tmdbAccountAvatarPath: String? = null,
    val tmdbAccountGravatarHash: String? = null,
    val isTmdbAccountLoading: Boolean = false,
    val isTmdbAccountConnected: Boolean = false,
    val isTmdbAccountApprovalPending: Boolean = false,
    val isTmdbWatchlistSyncEnabled: Boolean = true,
    val isTmdbRatingsSyncEnabled: Boolean = true,
    val tmdbLastSyncedAtMillis: Long? = null,
    val isOnline: Boolean = true,
    val appVersionLabel: String = "1.0.0 (build 1)",
    val appConfigurationLabel: String = "Production Debug · Token configured",
    val privacyLabel: String = "How we handle your data",
    val activePreferenceDialog: SettingsPreferenceDialog? = null,
    val isClearImageCacheDialogVisible: Boolean = false,
    val isPrivacyNoticeVisible: Boolean = false,
)
