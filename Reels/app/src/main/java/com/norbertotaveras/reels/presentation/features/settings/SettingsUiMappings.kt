/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import com.norbertotaveras.reels.domain.account.model.TmdbAccountStateDataModel
import com.norbertotaveras.reels.domain.library.model.LibraryContentDataModel
import com.norbertotaveras.reels.domain.settings.model.AppSettings
import com.norbertotaveras.reels.presentation.ui.extensions.displayLabel

internal fun SettingsUiState.withSettings(settings: AppSettings): SettingsUiState {
    return copy(
        imageQualityLabel = settings.imageQuality.displayLabel,
        languageRegionLabel = settings.languageRegion.displayLabel,
        themeLabel = settings.theme.displayLabel,
        selectedImageQuality = settings.imageQuality,
        selectedLanguageRegion = settings.languageRegion,
        selectedTheme = settings.theme,
        planPreferences = settings.planPreferences,
    )
}

internal fun SettingsUiState.withLibraryContent(content: LibraryContentDataModel): SettingsUiState {
    return copy(
        savedLibraryLabel =
            buildSavedLibraryLabel(
                movieCount = content.movies.size,
                personCount = content.people.size,
            )
    )
}

internal fun SettingsUiState.withTmdbAccountState(
    accountState: TmdbAccountStateDataModel
): SettingsUiState {
    val accountName =
        accountState.account?.displayName?.takeIf { displayName -> displayName.isNotBlank() }
            ?: accountState.account?.username
            ?: ""

    return copy(
        isTmdbAccountConnected = accountState.isConnected,
        isTmdbAccountApprovalPending = accountState.isApprovalPending,
        isTmdbWatchlistSyncEnabled = accountState.isWatchlistSyncEnabled,
        isTmdbRatingsSyncEnabled = accountState.isRatingsSyncEnabled,
        tmdbAccountName = accountName,
        tmdbAccountUsername = accountState.account?.username.orEmpty(),
        tmdbAccountAvatarPath = accountState.account?.avatarPath,
        tmdbAccountGravatarHash = accountState.account?.gravatarHash,
    )
}
