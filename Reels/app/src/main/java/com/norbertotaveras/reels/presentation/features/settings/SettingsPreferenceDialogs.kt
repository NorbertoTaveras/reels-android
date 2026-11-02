/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun LanguageRegionDialog(
    selectedOption: LanguageRegion,
    onOptionSelected: (LanguageRegion) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingOption by remember(selectedOption) { mutableStateOf(selectedOption) }

    SettingsDialog(
        icon = Icons.Filled.Language,
        title = "Language & Region",
        onDismiss = onDismiss,
        dismissLabel = "Cancel",
        confirmLabel = "Apply",
        onConfirm = { onOptionSelected(pendingOption) },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier.heightIn(max = ReelsDimensions.settingsDialogContentMaxHeight)
                    .verticalScroll(rememberScrollState())
        ) {
            DialogDescription(text = "Sets the language for movie titles and metadata.")

            Spacer(modifier = Modifier.height(ReelsSpacing.large))
            PrivacySectionLabel(text = "LANGUAGE")
            Spacer(modifier = Modifier.height(ReelsSpacing.small))

            Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                LanguageRegion.entries.forEach { option ->
                    SettingsOptionRow(
                        label = option.languageName,
                        description = option.regionName,
                        selected = option == pendingOption,
                        onClick = { pendingOption = option },
                    )
                }
            }

            Spacer(modifier = Modifier.height(ReelsSpacing.medium))
            PrivacySectionLabel(text = "REGION")
            Spacer(modifier = Modifier.height(ReelsSpacing.small))

            RegionOptionRow()
        }
    }
}

@Composable
internal fun <T> SettingsOptionDialog(
    title: String,
    description: String,
    icon: ImageVector,
    options: List<T>,
    selectedOption: T,
    optionLabel: (T) -> String,
    optionDescription: (T) -> String,
    modifier: Modifier = Modifier,
    optionIcon: ((T) -> ImageVector)? = null,
    confirmLabel: String = "Apply",
    supportingContent: (@Composable () -> Unit)? = null,
    onOptionSelected: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    var pendingOption by remember(selectedOption) { mutableStateOf(selectedOption) }

    SettingsDialog(
        icon = icon,
        title = title,
        onDismiss = onDismiss,
        dismissLabel = "Cancel",
        confirmLabel = confirmLabel,
        onConfirm = { onOptionSelected(pendingOption) },
        modifier = modifier,
    ) {
        Column {
            DialogDescription(text = description)

            Spacer(modifier = Modifier.height(ReelsSpacing.large))

            Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                options.forEach { option ->
                    SettingsOptionRow(
                        label = optionLabel(option),
                        description = optionDescription(option),
                        icon = optionIcon?.invoke(option),
                        selected = option == pendingOption,
                        onClick = { pendingOption = option },
                    )
                }
            }

            supportingContent?.let { content ->
                Spacer(modifier = Modifier.height(ReelsSpacing.large))
                content()
            }
        }
    }
}

@Composable
private fun DialogDescription(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style =
            MaterialTheme.typography.bodyMedium.copy(
                fontSize = ReelsDimensions.settingsDialogBodySize,
                lineHeight = ReelsDimensions.settingsDialogBodyLineHeight,
            ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun SettingsOptionRow(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(ReelsDimensions.settingsDialogOptionCornerSize))
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .clickable(onClick = onClick)
                .padding(
                    horizontal = ReelsDimensions.settingsDialogOptionHorizontalPadding,
                    vertical = ReelsDimensions.settingsDialogOptionVerticalPadding,
                ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.settingsDialogOptionSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsDialogRadio(selected = selected)

        icon?.let { optionIcon -> DialogOptionIcon(icon = optionIcon) }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(ReelsDimensions.settingsDialogOptionTextSpacing),
        ) {
            Text(
                text = label,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontSize = ReelsDimensions.settingsDialogOptionTitleSize,
                        lineHeight = ReelsDimensions.settingsDialogOptionTitleLineHeight,
                        fontWeight = FontWeight.Medium,
                    ),
                color =
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
            )

            if (description.isNotEmpty())
                Text(
                    text = description,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontSize = ReelsDimensions.settingsDialogOptionBodySize,
                            lineHeight = ReelsDimensions.settingsDialogOptionBodyLineHeight,
                            fontWeight = FontWeight.Medium,
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
        }
    }
}

@Composable
private fun RegionOptionRow(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ReelsDimensions.settingsDialogOptionCornerSize),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(ReelsSpacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsDialogInfoIconSize),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.width(ReelsSpacing.medium))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(ReelsDimensions.settingsDialogOptionTextSpacing),
            ) {
                Text(
                    text = "All regions",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontSize = ReelsDimensions.settingsDialogOptionTitleSize,
                            lineHeight = ReelsDimensions.settingsDialogOptionTitleLineHeight,
                            fontWeight = FontWeight.Medium,
                        ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Movie availability varies by region",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontSize = ReelsDimensions.settingsDialogOptionBodySize,
                            lineHeight = ReelsDimensions.settingsDialogOptionBodyLineHeight,
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsDialogChevronSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
