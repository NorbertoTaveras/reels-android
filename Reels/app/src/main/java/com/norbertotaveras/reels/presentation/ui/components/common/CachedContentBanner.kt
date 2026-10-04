/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
fun CachedContentBanner(
    message: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    onDismiss: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsSpacing.medium,
                vertical = ReelsSpacing.small
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.cachedContentIconContainerSize),
                shape = RoundedCornerShape(ReelsDimensions.cachedContentIconCornerSize),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.cachedContentIconSize)
                    )
                }
            }

            Text(
                text = listOfNotNull(message, supportingText)
                    .joinToString(separator = " · "),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall
            )

            onDismiss?.let { dismiss ->
                IconButton(
                    onClick = dismiss,
                    modifier = Modifier.size(ReelsDimensions.cachedContentDismissButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss cached content notice",
                        modifier = Modifier.size(ReelsDimensions.cachedContentDismissIconSize)
                    )
                }
            }
        }
    }
}
