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
                optionLabel = ImageQuality::label,
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
                optionLabel = { option ->
                    when (option) {
                        AppTheme.System -> "System default"
                        AppTheme.Dark -> "Dark"
                        AppTheme.Light -> "Light"
                    }
                },
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

@Composable
private fun ClearImageCacheDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier.widthIn(max = 360.dp),
        onDismissRequest = onDismiss,
        icon = {
            DialogIcon(
                icon = Icons.Filled.DeleteOutline,
                destructive = true
            )
        },
        title = {
            Text(
                text = "Clear Image Cache?",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)) {
                Text(
                    text = "This will remove 42 MB of cached poster images from your device. " +
                        "They will be re-downloaded the next time you browse.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(ReelsSpacing.large),
                        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
                    ) {
                        CacheBreakdownRow(
                            icon = Icons.Filled.Movie,
                            label = "Movie posters",
                            value = "28 MB"
                        )
                        CacheBreakdownRow(
                            icon = Icons.Filled.Person,
                            label = "Person portraits",
                            value = "11 MB"
                        )
                        CacheBreakdownRow(
                            icon = Icons.Filled.Image,
                            label = "Backdrop images",
                            value = "3 MB"
                        )
                    }
                }

                Text(
                    text = "Saved offline content is not affected.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Clear cache",
                    color = MaterialTheme.colorScheme.error
                )
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
        modifier = modifier.widthIn(max = 360.dp),
        onDismissRequest = onDismiss,
        icon = {
            DialogIcon(icon = Icons.Filled.Shield)
        },
        title = {
            Text(
                text = "Privacy Notice",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                Text(
                    text = "Last updated September 2026",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                PrivacyNoticeList(
                    title = "WHAT WE COLLECT",
                    items = listOf(
                        PrivacyNoticeItem(
                            title = "Search queries",
                            description = "Stored locally only, never sent to our servers."
                        ),
                        PrivacyNoticeItem(
                            title = "Saved movies",
                            description = "Stored on-device. Synced to TMDB only when connected."
                        ),
                        PrivacyNoticeItem(
                            title = "App preferences",
                            description = "Theme, quality, and language settings stored locally."
                        )
                    ),
                    included = true
                )

                PrivacyNoticeList(
                    title = "WHAT WE DON'T COLLECT",
                    items = listOf(
                        PrivacyNoticeItem(title = "Personal identity or contact information"),
                        PrivacyNoticeItem(title = "Precise location data"),
                        PrivacyNoticeItem(title = "Advertising identifiers"),
                        PrivacyNoticeItem(title = "Cross-app tracking")
                    ),
                    included = false
                )

                Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
                    PrivacySectionLabel(text = "THIRD PARTIES")

                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier.padding(ReelsSpacing.medium),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "The Movie Database (TMDB)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "When connected, your watchlist and ratings are synced " +
                                    "with TMDB under their privacy policy. Reels does not share " +
                                    "data with any other third party.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun PrivacyNoticeList(
    title: String,
    items: List<PrivacyNoticeItem>,
    included: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        PrivacySectionLabel(text = title)

        items.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (included) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(14.dp),
                    tint = if (included) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    item.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacySectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold
    )
}

private data class PrivacyNoticeItem(
    val title: String,
    val description: String? = null,
)

@Composable
private fun LanguageRegionDialog(
    selectedOption: LanguageRegion,
    onOptionSelected: (LanguageRegion) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingOption by remember(selectedOption) { mutableStateOf(selectedOption) }

    AlertDialog(
        modifier = modifier.widthIn(max = 360.dp),
        onDismissRequest = onDismiss,
        icon = {
            DialogIcon(icon = Icons.Filled.Language)
        },
        title = {
            Text(
                text = "Language & Region",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                Text(
                    text = "Sets the language for movie titles and metadata.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                PrivacySectionLabel(text = "LANGUAGE")

                Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
                    LanguageRegion.entries.forEach { option ->
                        SettingsOptionRow(
                            label = option.languageName,
                            description = option.regionName,
                            selected = option == pendingOption,
                            onClick = { pendingOption = option }
                        )
                    }
                }

                PrivacySectionLabel(text = "REGION")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(ReelsSpacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OptionIcon(icon = Icons.Filled.Language)

                        Spacer(modifier = Modifier.width(ReelsSpacing.medium))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "All regions",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Movie availability varies by region",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onOptionSelected(pendingOption) }) {
                Text(text = "Apply")
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

private val LanguageRegion.languageName: String
    get() = when (this) {
        LanguageRegion.EnglishUnitedStates -> "English"
        LanguageRegion.SpanishSpain -> "Español"
        LanguageRegion.FrenchFrance -> "Français"
        LanguageRegion.JapaneseJapan -> "日本語"
    }

private val LanguageRegion.regionName: String
    get() = when (this) {
        LanguageRegion.EnglishUnitedStates -> "United States"
        LanguageRegion.SpanishSpain -> "España"
        LanguageRegion.FrenchFrance -> "France"
        LanguageRegion.JapaneseJapan -> "日本"
    }

@Composable
private fun <T> SettingsOptionDialog(
    title: String,
    description: String,
    icon: ImageVector,
    options: List<T>,
    selectedOption: T,
    optionLabel: (T) -> String,
    optionDescription: (T) -> String,
    optionIcon: ((T) -> ImageVector)? = null,
    confirmLabel: String = "Apply",
    supportingContent: (@Composable () -> Unit)? = null,
    onOptionSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingOption by remember(selectedOption) { mutableStateOf(selectedOption) }

    AlertDialog(
        modifier = modifier.widthIn(max = 360.dp),
        onDismissRequest = onDismiss,
        icon = {
            DialogIcon(icon = icon)
        },
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
                    options.forEach { option ->
                        SettingsOptionRow(
                            label = optionLabel(option),
                            description = optionDescription(option),
                            icon = optionIcon?.invoke(option),
                            selected = option == pendingOption,
                            onClick = { pendingOption = option }
                        )
                    }
                }

                supportingContent?.invoke()
            }
        },
        confirmButton = {
            TextButton(onClick = { onOptionSelected(pendingOption) }) {
                Text(text = confirmLabel)
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
private fun SettingsOptionRow(
    label: String,
    description: String,
    icon: ImageVector? = null,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ReelsDimensions.settingsOptionRowMinHeight)
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = ReelsSpacing.medium,
                vertical = ReelsSpacing.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            OptionIcon(icon = icon)
            Spacer(modifier = Modifier.width(ReelsSpacing.medium))
        }

        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(ReelsSpacing.small))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OptionIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsInfoBanner(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(ReelsSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DialogIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    val contentColor = if (destructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(MaterialTheme.shapes.large)
            .background(contentColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )
    }
}

@Composable
private fun CacheBreakdownRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
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
