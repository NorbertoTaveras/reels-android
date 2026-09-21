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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.model.settings.AppTheme
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
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
                title = "Saved Movies",
                subtitle = state.savedMoviesLabel,
                onClick = { onEvent(SettingsEvent.SavedMoviesClicked) }
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.DeleteOutline,
                title = "Clear Image Cache",
                subtitle = state.clearImageCacheLabel,
                onClick = { onEvent(SettingsEvent.ClearImageCacheClicked) }
            )
        }

        SettingsSection(title = "ABOUT REELS") {
            SettingsRow(
                icon = Icons.Filled.Info,
                title = "App Version",
                subtitle = state.appVersionLabel,
                showsChevron = false
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.Shield,
                title = "Privacy",
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
                description = "Choose how large TMDB poster and backdrop images should be when Reels loads movie artwork.",
                options = ImageQuality.values().toList(),
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
                options = LanguageRegion.values().toList(),
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
                options = AppTheme.values().toList(),
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
                text = "Reels will remove locally cached poster and backdrop images. Movie data and favorites will stay saved.",
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
                    text = "Favorites and preferences stay on this device."
                )

                PrivacyNoticeText(
                    text = "Movie data, artwork, cast details, and trailers are loaded from TMDB as you browse."
                )

                PrivacyNoticeText(
                    text = "Reels does not use TMDB account login, collect personal account data, or sync your favorites to TMDB."
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
    Box(
        modifier = modifier
            .size(ReelsDimensions.settingsTmdbLogoSize)
            .clip(MaterialTheme.shapes.large)
            .background(Color(0xFF01B4E4)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TM",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(2.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(ReelsDimensions.settingsTmdbLogoPillWidth)
                        .height(ReelsDimensions.settingsTmdbLogoPillHeight)
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.White)
                )
                Text(
                    text = "D",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
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
