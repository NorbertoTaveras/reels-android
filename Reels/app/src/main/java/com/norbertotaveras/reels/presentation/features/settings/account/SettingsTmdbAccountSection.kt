/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.presentation.features.settings.SettingsSectionLabel
import com.norbertotaveras.reels.presentation.features.settings.SettingsUiState
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun TmdbAccountSection(
    state: SettingsUiState,
    onConnectClick: () -> Unit,
    onFinishClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onWatchlistSyncChanged: (Boolean) -> Unit,
    onRatingsSyncChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.settingsSectionContentSpacing)
    ) {
        SettingsSectionLabel(text = "TMDB ACCOUNT")

        when {
            state.isTmdbAccountConnected -> {
                ConnectedTmdbAccountCard(
                    accountName = state.tmdbAccountUsername.ifBlank {
                        state.tmdbAccountName.ifBlank { "TMDB user" }
                    },
                    avatarPath = state.tmdbAccountAvatarPath,
                    gravatarHash = state.tmdbAccountGravatarHash,
                    isLoading = state.isTmdbAccountLoading,
                    isWatchlistSyncEnabled = state.isTmdbWatchlistSyncEnabled,
                    isRatingsSyncEnabled = state.isTmdbRatingsSyncEnabled,
                    lastSyncedAtMillis = state.tmdbLastSyncedAtMillis,
                    isOnline = state.isOnline,
                    onDisconnectClick = onDisconnectClick,
                    onWatchlistSyncChanged = onWatchlistSyncChanged,
                    onRatingsSyncChanged = onRatingsSyncChanged
                )
            }

            state.isTmdbAccountApprovalPending -> {
                PendingTmdbAccountCard(
                    isLoading = state.isTmdbAccountLoading,
                    onFinishClick = onFinishClick
                )
            }

            else -> {
                DisconnectedTmdbAccountCard(
                    isLoading = state.isTmdbAccountLoading,
                    onConnectClick = onConnectClick
                )
            }
        }
    }
}
