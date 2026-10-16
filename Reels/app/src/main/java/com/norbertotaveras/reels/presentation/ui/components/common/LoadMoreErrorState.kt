/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
fun LoadMoreErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = reelsBorderColor())
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = ReelsDimensions.loadMoreErrorVerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                ReelsDimensions.loadMoreErrorContentSpacing
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    ReelsDimensions.loadMoreErrorContentSpacing
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.loadMoreErrorIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Couldn't load more movies",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        lineHeight = 16.5f.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.height(ReelsDimensions.loadMoreErrorActionHeight),
                shape = RoundedCornerShape(ReelsDimensions.loadMoreErrorActionCornerSize),
                contentPadding = PaddingValues(
                    horizontal = ReelsDimensions.loadMoreErrorActionHorizontalPadding,
                    vertical = ReelsDimensions.loadMoreErrorActionVerticalPadding
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        ReelsDimensions.loadMoreErrorActionContentSpacing
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.loadMoreErrorActionIconSize)
                    )
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 11.sp,
                            lineHeight = 16.5f.sp
                        ),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
