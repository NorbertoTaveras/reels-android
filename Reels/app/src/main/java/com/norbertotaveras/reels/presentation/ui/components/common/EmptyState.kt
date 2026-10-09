/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    MessageState(
        modifier = modifier,
        icon = Icons.Outlined.Movie,
        title = title,
        message = message
    )
}
