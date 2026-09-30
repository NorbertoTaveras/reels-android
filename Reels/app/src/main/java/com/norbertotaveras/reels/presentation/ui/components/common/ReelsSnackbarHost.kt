/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
fun ReelsSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { snackbarData ->
        val message = snackbarData.visuals.message
        val visual = snackbarVisualFor(message = message)

        Surface(
            modifier = Modifier
                .padding(horizontal = ReelsSpacing.large, vertical = ReelsSpacing.medium)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = ReelsSpacing.medium,
                    vertical = ReelsSpacing.medium
                ),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                SnackbarLeadingVisual(
                    visual = visual,
                    contentDescription = null
                )

                Text(
                    text = message,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                snackbarData.visuals.actionLabel?.let { actionLabel ->
                    TextButton(
                        onClick = snackbarData::performAction,
                        modifier = Modifier.padding(top = 0.dp)
                    ) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = ReelsAccent
                        )
                    }
                }

                IconButton(
                    onClick = snackbarData::dismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SnackbarLeadingVisual(
    visual: SnackbarVisual,
    contentDescription: String?,
) {
    if (visual.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = visual.tint,
            strokeWidth = 2.dp
        )
    } else {
        Icon(
            imageVector = visual.icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(22.dp),
            tint = visual.tint
        )
    }
}
