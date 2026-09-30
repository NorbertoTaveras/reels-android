/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbBlue
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbOnBlue

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

@Composable
private fun DisconnectedTmdbAccountCard(
    isLoading: Boolean,
    onConnectClick: () -> Unit,
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
            .padding(ReelsDimensions.settingsTmdbCardPadding),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TmdbLogoMark()

            Spacer(modifier = Modifier.width(ReelsSpacing.large))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Connect to TMDB",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sync watchlist and ratings",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = onConnectClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = ReelsTmdbBlue,
                contentColor = ReelsTmdbOnBlue
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = ReelsTmdbOnBlue
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(ReelsSpacing.small))

                Text(text = "Sign in with TMDB")
            }
        }
    }
}

@Composable
private fun PendingTmdbAccountCard(
    isLoading: Boolean,
    onFinishClick: () -> Unit,
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
            .padding(ReelsDimensions.settingsTmdbCardPadding),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        TmdbAccountHeaderRow(
            title = "Approval pending",
            subtitle = "Approve Reels on themoviedb.org, then finish here."
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = onFinishClick
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Finish Connection")
            }
        }
    }
}

@Composable
private fun ConnectedTmdbAccountCard(
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

@Composable
private fun TmdbAccountHeaderRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.settingsRowHorizontalPadding,
                vertical = ReelsDimensions.settingsRowVerticalPadding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TmdbLogoMark()

        Spacer(modifier = Modifier.width(ReelsSpacing.large))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (trailing != null) {
            Spacer(modifier = Modifier.width(ReelsSpacing.medium))
            trailing()
        }
    }
}

@Composable
private fun TmdbAccountStatusRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val rowModifier = modifier
        .fillMaxWidth()
        .heightIn(min = ReelsDimensions.settingsRowMinHeight)
        .then(
            if (onClick != null) {
                Modifier.clickable(onClick = onClick)
            } else {
                Modifier
            }
        )

    Row(
        modifier = rowModifier.padding(
            horizontal = ReelsDimensions.settingsRowHorizontalPadding,
            vertical = ReelsDimensions.settingsRowVerticalPadding
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ReelsDimensions.settingsRowIconContainerSize)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsRowIconSize),
                tint = contentColor
            )
        }

        Spacer(modifier = Modifier.width(ReelsSpacing.large))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (trailing != null) {
            Spacer(modifier = Modifier.width(ReelsSpacing.medium))
            trailing()
        }
    }
}

@Composable
private fun ConnectedChip(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = ReelsTeal.copy(alpha = 0.16f),
        contentColor = ReelsTeal
    ) {
        Text(
            text = "Connected",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StaticSettingsSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(width = 42.dp, height = 24.dp),
        shape = MaterialTheme.shapes.large,
        color = if (checked) {
            ReelsTeal.copy(alpha = 0.32f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(if (checked) ReelsTeal else MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}
