/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun DiscoverGenreErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ReelsDimensions.genreErrorCornerSize),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.genreErrorHorizontalPadding,
                vertical = ReelsDimensions.genreErrorVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(
                ReelsDimensions.genreErrorContentSpacing
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.genreErrorIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    ReelsDimensions.genreErrorTextSpacing
                )
            ) {
                Text(
                    text = "Couldn't load genres",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.sp,
                        lineHeight = 16.5f.sp
                    ),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Check your connection",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.height(ReelsDimensions.genreErrorActionHeight),
                shape = RoundedCornerShape(ReelsDimensions.genreErrorActionCornerSize),
                contentPadding = PaddingValues(
                    horizontal = ReelsDimensions.genreErrorActionHorizontalPadding,
                    vertical = ReelsDimensions.genreErrorActionVerticalPadding
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        ReelsDimensions.genreErrorActionContentSpacing
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.genreErrorActionIconSize)
                    )
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            lineHeight = 15.sp
                        ),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
