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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
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
        val reelsVisuals = snackbarData.visuals as? ReelsSnackbarVisuals
        val message = snackbarData.visuals.message
        val visual = snackbarVisualFor(
            message = listOfNotNull(
                message,
                snackbarData.visuals.actionLabel
            ).joinToString(separator = " ")
        )

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
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SnackbarLeadingVisual(
                    visual = visual,
                    contentDescription = null
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = ReelsDimensions.snackbarTextTopPadding),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

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

                if (reelsVisuals?.withDismissAction ?: !visual.isLoading) {
                    IconButton(
                        onClick = snackbarData::dismiss,
                        modifier = Modifier.size(ReelsDimensions.snackbarCloseButtonSize)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier.size(ReelsDimensions.snackbarCloseIconSize),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
            modifier = Modifier.size(ReelsDimensions.snackbarProgressSize),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            strokeWidth = ReelsDimensions.snackbarProgressStrokeWidth
        )
    } else if (visual.usesContainer) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.snackbarIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.snackbarIconContainerCornerSize),
            color = visual.tint.copy(alpha = 0.12f),
            contentColor = visual.tint
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(ReelsDimensions.snackbarIconSize),
                    tint = visual.tint
                )
            }
        }
    } else {
        Icon(
            imageVector = visual.icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(ReelsDimensions.snackbarStandaloneIconSize),
            tint = visual.tint
        )
    }
}
