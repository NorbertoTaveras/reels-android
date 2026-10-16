/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
fun MessageState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    actionIcon: ImageVector? = null,
    onAction: (() -> Unit)? = null,
    verticalPadding: Dp = ReelsDimensions.stateVerticalPadding,
    actionHeight: Dp = ReelsDimensions.stateActionHeight,
    actionHorizontalPadding: Dp = ReelsDimensions.stateActionHorizontalPadding,
    actionVerticalPadding: Dp = ReelsDimensions.stateActionVerticalPadding,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.stateHorizontalPadding,
                vertical = verticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.stateIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.stateIconContainerCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.stateIconSize)
                )
            }
        }

        Text(
            modifier = Modifier.padding(top = ReelsDimensions.stateIconToTitleSpacing),
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = ReelsDimensions.stateTitleSize,
                lineHeight = ReelsDimensions.stateTitleLineHeight
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = ReelsDimensions.stateTitleToMessageSpacing),
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = ReelsDimensions.stateMessageSize,
                lineHeight = ReelsDimensions.stateMessageLineHeight
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (actionLabel != null && onAction != null) {
            Button(
                modifier = Modifier
                    .padding(top = ReelsDimensions.stateMessageToActionSpacing)
                    .height(actionHeight),
                onClick = onAction,
                shape = RoundedCornerShape(ReelsDimensions.stateActionCornerSize),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(
                    horizontal = actionHorizontalPadding,
                    vertical = actionVerticalPadding
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        ReelsDimensions.stateActionContentSpacing
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actionIcon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            modifier = Modifier.size(ReelsDimensions.stateActionIconSize)
                        )
                    }
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = ReelsDimensions.stateActionTextSize,
                            lineHeight = ReelsDimensions.stateActionTextLineHeight
                        ),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
