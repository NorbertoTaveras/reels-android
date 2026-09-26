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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

private val ReelsTmdbBlue = Color(0xFF01B4E4)
private val ReelsTmdbOnBlue = Color.White

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ReelsSpacing.screenHorizontal)
            .padding(
                top = ReelsDimensions.settingsScreenTopPadding,
                bottom = ReelsDimensions.settingsScreenBottomPadding
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.settingsSectionSpacing)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        TmdbAccountSection(
            state = state,
            onConnectClick = { onEvent(SettingsEvent.TmdbAccountClicked) },
            onFinishClick = { onEvent(SettingsEvent.TmdbAccountFinishClicked) },
            onDisconnectClick = { onEvent(SettingsEvent.TmdbAccountDisconnectClicked) }
        )

        SettingsSection(title = "APP PREFERENCES") {
            SettingsRow(
                icon = Icons.Filled.Image,
                title = "Image Quality",
                subtitle = state.imageQualityLabel,
                onClick = { onEvent(SettingsEvent.ImageQualityClicked) }
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.Language,
                title = "Language & Region",
                subtitle = state.languageRegionLabel,
                onClick = { onEvent(SettingsEvent.LanguageRegionClicked) }
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.Palette,
                title = "Theme",
                subtitle = state.themeLabel,
                onClick = { onEvent(SettingsEvent.ThemeClicked) }
            )
        }

        SettingsSection(title = "DATA & STORAGE") {
            SettingsRow(
                icon = Icons.Filled.Download,
                title = "Saved Library",
                subtitle = state.savedLibraryLabel,
                onClick = { onEvent(SettingsEvent.SavedLibraryClicked) }
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.DeleteOutline,
                title = "Clear Image Cache",
                subtitle = state.clearImageCacheLabel,
                onClick = { onEvent(SettingsEvent.ClearImageCacheClicked) }
            )
        }

        SettingsSection(title = "ABOUT") {
            SettingsRow(
                icon = Icons.Filled.Info,
                title = "App Version",
                subtitle = state.appVersionLabel,
                showsChevron = false
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.VerifiedUser,
                title = "UI States Reference",
                subtitle = "Production state catalogue",
                onClick = { onEvent(SettingsEvent.UiStatesReferenceClicked) }
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.Shield,
                title = "Privacy Notice",
                subtitle = state.privacyLabel,
                onClick = { onEvent(SettingsEvent.PrivacyClicked) }
            )
        }

        TmdbAttributionSection(
            onClick = { onEvent(SettingsEvent.TmdbAttributionClicked) }
        )
    }

    if (state.isPrivacyNoticeVisible) {
        PrivacyNoticeDialog(
            onDismiss = { onEvent(SettingsEvent.PrivacyNoticeDismissed) }
        )
    }

    if (state.isClearImageCacheDialogVisible) {
        ClearImageCacheDialog(
            onConfirm = { onEvent(SettingsEvent.ClearImageCacheConfirmed) },
            onDismiss = { onEvent(SettingsEvent.ClearImageCacheDismissed) }
        )
    }

    when (state.activePreferenceDialog) {
        SettingsPreferenceDialog.ImageQuality -> {
            SettingsOptionDialog(
                title = "Image Quality",
                description = "Choose how large TMDB poster, backdrop, and profile images should be when Reels loads artwork.",
                options = ImageQuality.entries,
                selectedOption = state.selectedImageQuality,
                optionLabel = ImageQuality::label,
                onOptionSelected = { imageQuality ->
                    onEvent(SettingsEvent.ImageQualitySelected(imageQuality))
                },
                onDismiss = { onEvent(SettingsEvent.PreferenceDialogDismissed) }
            )
        }

        SettingsPreferenceDialog.LanguageRegion -> {
            SettingsOptionDialog(
                title = "Language & Region",
                description = "Choose the language and region Reels sends to TMDB for browse, search, and detail requests.",
                options = LanguageRegion.entries,
                selectedOption = state.selectedLanguageRegion,
                optionLabel = LanguageRegion::label,
                onOptionSelected = { languageRegion ->
                    onEvent(SettingsEvent.LanguageRegionSelected(languageRegion))
                },
                onDismiss = { onEvent(SettingsEvent.PreferenceDialogDismissed) }
            )
        }

        SettingsPreferenceDialog.Theme -> {
            SettingsOptionDialog(
                title = "Theme",
                description = "Choose whether Reels follows the device theme or uses a fixed light or dark appearance.",
                options = AppTheme.entries,
                selectedOption = state.selectedTheme,
                optionLabel = AppTheme::label,
                onOptionSelected = { theme ->
                    onEvent(SettingsEvent.ThemeSelected(theme))
                },
                onDismiss = { onEvent(SettingsEvent.PreferenceDialogDismissed) }
            )
        }

        null -> Unit
    }
}

@Composable
private fun ClearImageCacheDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clear Image Cache?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Reels will remove locally cached poster, backdrop, and profile images. Movie data and your saved library will stay saved.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Clear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun PrivacyNoticeDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Privacy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                PrivacyNoticeText(
                    text = "Saved movies, saved people, and preferences stay on this device."
                )

                PrivacyNoticeText(
                    text = "Movie data, artwork, cast details, and trailers are loaded from TMDB as you browse."
                )

                PrivacyNoticeText(
                    text = "If you connect TMDB, Reels stores the TMDB session only on this device so watchlist and rating sync can work."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Got it")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun PrivacyNoticeText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun <T> SettingsOptionDialog(
    title: String,
    description: String,
    options: List<T>,
    selectedOption: T,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column {
                    options.forEach { option ->
                        SettingsOptionRow(
                            label = optionLabel(option),
                            selected = option == selectedOption,
                            onClick = { onOptionSelected(option) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SettingsOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ReelsDimensions.settingsOptionRowMinHeight)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = ReelsDimensions.settingsOptionRowHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(ReelsSpacing.small))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
private fun TmdbAccountSection(
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
                    text = "The Movie Database",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Connect to sync watchlist and ratings",
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

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.settingsSectionContentSpacing)
    ) {
        Text(
            text = title,
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

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.extraLarge
                ),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    showsChevron: Boolean = true,
    onClick: (() -> Unit)? = null,
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
        modifier = rowModifier
            .padding(
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
                tint = ReelsAccent
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

        if (showsChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsChevronSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = ReelsDimensions.settingsDividerThickness,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun TmdbAttributionSection(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Text(
            text = "TMDB ATTRIBUTION",
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

        TmdbAttributionCard(onClick = onClick)
    }
}

@Composable
private fun TmdbAttributionCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "The Movie Database",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Powered by TMDB API",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "This product uses the TMDB API but is not endorsed or certified by TMDB.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Visit themoviedb.org",
                style = MaterialTheme.typography.bodySmall,
                color = ReelsAccent,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsExternalIconSize),
                tint = ReelsAccent
            )
        }
    }
}

@Composable
private fun TmdbLogoMark(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.settingsTmdbLogoSize)
            .clip(MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Image(
            painter = painterResource(id = R.drawable.tmdb_logo),
            contentDescription = "TMDB logo",
            modifier = Modifier
                .fillMaxSize()
                .padding(ReelsSpacing.small),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    ReelsTheme {
        SettingsScreen(
            state = SettingsUiState(),
            onEvent = {}
        )
    }
}
