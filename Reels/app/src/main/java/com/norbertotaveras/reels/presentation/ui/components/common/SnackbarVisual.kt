/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsError

internal data class SnackbarVisual(
    val icon: ImageVector,
    val tint: Color,
    val isLoading: Boolean = false,
)

internal fun snackbarVisualFor(message: String): SnackbarVisual {
    val normalizedMessage = message.lowercase()

    return when {
        normalizedMessage.contains("syncing") -> SnackbarVisual(
            icon = Icons.Filled.CloudSync,
            tint = ReelsAccent,
            isLoading = true
        )

        normalizedMessage.contains("rated") -> SnackbarVisual(
            icon = Icons.Filled.Star,
            tint = ReelsAccent
        )

        normalizedMessage.contains("unable") ||
            normalizedMessage.contains("error") ||
            normalizedMessage.contains("failed") ||
            normalizedMessage.contains("retry") -> SnackbarVisual(
                icon = Icons.Filled.ErrorOutline,
                tint = ReelsError
            )

        else -> SnackbarVisual(
            icon = Icons.Filled.CheckCircle,
            tint = ReelsAccent
        )
    }
}
