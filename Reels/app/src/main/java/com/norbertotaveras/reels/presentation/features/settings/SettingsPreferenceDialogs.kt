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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
internal fun <T> SettingsOptionDialog(
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
