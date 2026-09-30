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
internal fun PrivacyNoticeDialog(
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
internal fun PrivacySectionLabel(
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
