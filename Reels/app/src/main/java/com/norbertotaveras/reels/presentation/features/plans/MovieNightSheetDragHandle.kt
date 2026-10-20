/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun MovieNightSheetDragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = ReelsDimensions.movieNightSheetHandleTopPadding,
                bottom = ReelsDimensions.movieNightSheetHandleBottomPadding,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = ReelsDimensions.movieNightSheetHandleWidth,
                    height = ReelsDimensions.movieNightSheetHandleHeight,
                )
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), CircleShape),
        )
    }
}
