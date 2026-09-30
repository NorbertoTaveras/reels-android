/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun ConnectedTmdbAccountCard(
    accountName: String,
    isLoading: Boolean,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(vertical = ReelsSpacing.small),
    ) {
        TmdbAccountHeaderRow(
            title = accountName,
            subtitle = "TMDB account connected",
            trailing = {
                ConnectedChip()
            }
        )

        SettingsDivider()

        TmdbAccountStatusRow(
            icon = Icons.Filled.Sync,
            title = "Sync Status",
            subtitle = "Ready to sync watchlist and ratings"
        )

        SettingsDivider()

        TmdbAccountStatusRow(
            icon = Icons.Filled.Download,
            title = "Watchlist Sync",
            subtitle = "Enabled",
            trailing = {
                StaticSettingsSwitch(checked = true)
            }
        )

        SettingsDivider()

        TmdbAccountStatusRow(
            icon = Icons.Filled.Star,
            title = "Ratings Sync",
            subtitle = "Enabled",
            trailing = {
                StaticSettingsSwitch(checked = true)
            }
        )

        SettingsDivider()

        TmdbAccountStatusRow(
            icon = Icons.AutoMirrored.Filled.Logout,
            title = if (isLoading) "Disconnecting" else "Sign Out",
            subtitle = "Remove TMDB session from this device",
            contentColor = MaterialTheme.colorScheme.error,
            onClick = if (isLoading) null else onDisconnectClick,
            trailing = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}
