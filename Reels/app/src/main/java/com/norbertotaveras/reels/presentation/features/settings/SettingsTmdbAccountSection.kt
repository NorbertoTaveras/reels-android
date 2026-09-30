/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun TmdbAccountSection(
    state: SettingsUiState,
    onConnectClick: () -> Unit,
    onFinishClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.settingsSectionContentSpacing)
    ) {
        Text(
            text = "TMDB ACCOUNT",
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.settingsSectionTitleHorizontalPadding
            ),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = ReelsDimensions.settingsSectionTitleSize,
                letterSpacing = 0.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        when {
            state.isTmdbAccountConnected -> {
                ConnectedTmdbAccountCard(
                    accountName = state.tmdbAccountName.ifBlank { "TMDB user" },
                    isLoading = state.isTmdbAccountLoading,
                    onDisconnectClick = onDisconnectClick
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
