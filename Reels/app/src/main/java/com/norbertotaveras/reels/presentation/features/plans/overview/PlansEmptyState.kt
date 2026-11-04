/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.overview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsMutedContainerColor

@Composable
internal fun PlansEmptyState(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    actionLabel: String,
    isRelease: Boolean,
    onBrowse: () -> Unit,
) {
    Box(
        Modifier.fillMaxSize()
            .padding(horizontal = ReelsDimensions.plansEmptyHorizontalPadding)
            .padding(top = ReelsDimensions.plansEmptyTopPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(ReelsDimensions.plansEmptyIconContainerSize)
                    .background(
                        reelsMutedContainerColor(),
                        RoundedCornerShape(ReelsDimensions.plansEmptyIconCornerSize),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Spacer(Modifier.height(ReelsDimensions.plansEmptyTitleSpacing))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(ReelsDimensions.plansEmptySpacing))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(ReelsDimensions.plansEmptyActionTopSpacing))
            OutlinedButton(
                onClick = onBrowse,
                modifier = Modifier.height(ReelsDimensions.plansEmptyActionHeight),
                shape = RoundedCornerShape(ReelsDimensions.plansEmptyActionCornerSize),
                border = BorderStroke(ReelsDimensions.settingsDividerThickness, reelsBorderColor()),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                contentPadding = PaddingValues(horizontal = ReelsSpacing.xLarge),
            ) {
                Icon(
                    if (isRelease) Icons.Filled.Add else Icons.Filled.PlayArrow,
                    null,
                    modifier = Modifier.size(ReelsDimensions.settingsDialogInfoIconSize),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(actionLabel, modifier = Modifier.padding(start = ReelsSpacing.small))
            }
        }
    }
}
