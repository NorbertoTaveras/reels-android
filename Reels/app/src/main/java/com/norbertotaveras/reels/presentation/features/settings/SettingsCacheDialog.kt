/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun ClearImageCacheDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsDialog(
        icon = Icons.Filled.DeleteOutline,
        title = "Clear Image Cache?",
        onDismiss = onDismiss,
        dismissLabel = "Cancel",
        confirmLabel = "Clear cache",
        onConfirm = onConfirm,
        modifier = modifier,
        destructive = true
    ) {
        Column {
            Text(
                text = "This will remove 42 MB of cached poster images from your device. " +
                    "They will be re-downloaded the next time you browse.",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = ReelsDimensions.settingsDialogBodySize,
                    lineHeight = ReelsDimensions.settingsDialogBodyLineHeight
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(ReelsSpacing.large))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ReelsDimensions.settingsDialogOptionCornerSize),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column {
                    CacheBreakdownRow(
                        icon = Icons.Filled.Movie,
                        label = "Movie posters",
                        value = "28 MB"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    CacheBreakdownRow(
                        icon = Icons.Filled.Person,
                        label = "Person portraits",
                        value = "11 MB"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    CacheBreakdownRow(
                        icon = Icons.Filled.Image,
                        label = "Backdrop images",
                        value = "3 MB"
                    )
                }
            }

            Spacer(modifier = Modifier.height(ReelsSpacing.medium))

            Text(
                text = "Saved offline content is not affected.",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = ReelsDimensions.settingsDialogOptionBodySize,
                    lineHeight = ReelsDimensions.settingsDialogOptionBodyLineHeight
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
