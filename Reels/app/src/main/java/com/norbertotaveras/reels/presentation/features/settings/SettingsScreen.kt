/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.presentation.ui.extensions.displayLabel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

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
                description = "Higher quality uses more mobile data.",
                icon = Icons.Filled.Image,
                options = ImageQuality.entries,
                selectedOption = state.selectedImageQuality,
                optionLabel = { option -> option.displayLabel },
                optionDescription = { option ->
                    when (option) {
                        ImageQuality.High -> "Sharp posters · Uses the most data"
                        ImageQuality.Standard -> "Balanced quality · Recommended"
                        ImageQuality.Low -> "Reduced quality · Best on slow connections"
                    }
                },
                confirmLabel = "Save",
                supportingContent = {
                    SettingsInfoBanner(
                        text = "Saved offline movies always use the quality they were downloaded at."
                    )
                },
                onOptionSelected = { imageQuality ->
                    onEvent(SettingsEvent.ImageQualitySelected(imageQuality))
                },
                onDismiss = { onEvent(SettingsEvent.PreferenceDialogDismissed) }
            )
        }

        SettingsPreferenceDialog.LanguageRegion -> {
            LanguageRegionDialog(
                selectedOption = state.selectedLanguageRegion,
                onOptionSelected = { languageRegion ->
                    onEvent(SettingsEvent.LanguageRegionSelected(languageRegion))
                },
                onDismiss = { onEvent(SettingsEvent.PreferenceDialogDismissed) }
            )
        }

        SettingsPreferenceDialog.Theme -> {
            SettingsOptionDialog(
                title = "App Theme",
                description = "Choose how Reels looks on your device.",
                icon = Icons.Filled.Palette,
                options = AppTheme.entries,
                selectedOption = state.selectedTheme,
                optionLabel = { option -> option.displayLabel },
                optionDescription = { option ->
                    when (option) {
                        AppTheme.System -> "Follows your device's dark/light setting"
                        AppTheme.Dark -> "Cinematic dark theme"
                        AppTheme.Light -> "Cinema-inspired warm light"
                    }
                },
                optionIcon = { option ->
                    when (option) {
                        AppTheme.System -> Icons.Filled.Monitor
                        AppTheme.Dark -> Icons.Filled.DarkMode
                        AppTheme.Light -> Icons.Filled.LightMode
                    }
                },
                onOptionSelected = { theme ->
                    onEvent(SettingsEvent.ThemeSelected(theme))
                },
                onDismiss = { onEvent(SettingsEvent.PreferenceDialogDismissed) }
            )
        }

        null -> Unit
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
