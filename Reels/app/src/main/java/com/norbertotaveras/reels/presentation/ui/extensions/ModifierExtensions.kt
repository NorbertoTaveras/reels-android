/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

fun Modifier.fullBleedHorizontal(horizontalInset: Dp): Modifier {
    return layout { measurable, constraints ->
        val horizontalInsetPx = horizontalInset.roundToPx()
        val expandedWidth = constraints.maxWidth + (horizontalInsetPx * 2)
        val placeable = measurable.measure(
            constraints.copy(
                minWidth = expandedWidth,
                maxWidth = expandedWidth
            )
        )

        layout(width = constraints.maxWidth, height = placeable.height) {
            placeable.placeRelative(x = -horizontalInsetPx, y = 0)
        }
    }
}
