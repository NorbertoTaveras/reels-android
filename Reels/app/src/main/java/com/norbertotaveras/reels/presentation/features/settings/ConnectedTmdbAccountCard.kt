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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.extensions.toGravatarUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbSyncLabel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
internal fun ConnectedTmdbAccountCard(
    accountName: String,
    avatarPath: String?,
    gravatarHash: String?,
    isLoading: Boolean,
    isWatchlistSyncEnabled: Boolean,
    isRatingsSyncEnabled: Boolean,
    lastSyncedAtMillis: Long?,
    isOnline: Boolean,
    onDisconnectClick: () -> Unit,
    onWatchlistSyncChanged: (Boolean) -> Unit,
    onRatingsSyncChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(ReelsDimensions.settingsTmdbCardCornerSize)
    val avatarUrl = avatarPath.toTmdbImageUrl(
        imageType = TmdbImageType.Profile
    ) ?: gravatarHash.toGravatarUrl()
    val syncStatusLabel = lastSyncedAtMillis.toTmdbSyncLabel(
        prefix = "Last synced",
        missingLabel = "Not synced yet"
    ).let { label ->
        if (isOnline) label else "Offline · $label"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = ReelsDimensions.settingsDividerThickness,
                color = reelsBorderColor(),
                shape = cardShape
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = cardShape
            )
    ) {
        TmdbConnectedAccountHeaderRow(
            accountName = accountName,
            accountUrl = "tmdb.org/u/$accountName",
            avatarUrl = avatarUrl
        )

        SettingsDivider()

        TmdbAccountStatusRow(
            icon = Icons.Filled.Sync,
            title = "Sync Status",
            subtitle = syncStatusLabel,
            trailing = { TmdbSyncStatusChevron() }
        )

        SettingsDivider()

        TmdbAccountStatusRow(
            icon = Icons.Filled.Bookmark,
            title = "Watchlist Sync",
            subtitle = "Sync with TMDB account",
            trailing = {
                SettingsSyncSwitch(
                    checked = isWatchlistSyncEnabled,
                    label = "Watchlist Sync",
                    onCheckedChange = onWatchlistSyncChanged
                )
            }
        )

        SettingsDivider()

        TmdbAccountStatusRow(
            icon = Icons.Filled.Star,
            title = "Ratings Sync",
            subtitle = "Sync your movie ratings",
            trailing = {
                SettingsSyncSwitch(
                    checked = isRatingsSyncEnabled,
                    label = "Ratings Sync",
                    onCheckedChange = onRatingsSyncChanged
                )
            }
        )

        SettingsDivider()

        TmdbAccountStatusRow(
            icon = Icons.AutoMirrored.Filled.Logout,
            title = if (isLoading) "Disconnecting" else "Sign out",
            subtitle = "Remove TMDB connection",
            titleColor = MaterialTheme.colorScheme.error,
            iconTint = MaterialTheme.colorScheme.error,
            iconContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.10f),
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
