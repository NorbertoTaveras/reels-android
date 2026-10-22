/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider

@Composable
internal fun WidgetIcon(icon: ImageVector, size: Dp, tint: ColorProvider) {
    CompositionLocalProvider(
        LocalDensity provides Density(ICON_DENSITY),
        LocalLayoutDirection provides LayoutDirection.Ltr,
    ) {
        WidgetIconContent(icon, size, tint)
    }
}

@Composable
private fun WidgetIconContent(icon: ImageVector, size: Dp, tint: ColorProvider) {
    val painter = rememberVectorPainter(icon)
    val bitmap = remember(painter, size) {
        // Rasterize the existing icon library for RemoteViews at a crisp launcher density.
        val pixels = (size.value * ICON_DENSITY).toInt().coerceAtLeast(1)
        Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888).also { bitmap ->
            val bounds = Size(pixels.toFloat(), pixels.toFloat())
            CanvasDrawScope().draw(
                density = Density(ICON_DENSITY),
                layoutDirection = LayoutDirection.Ltr,
                canvas = Canvas(bitmap.asImageBitmap()),
                size = bounds,
            ) {
                with(painter) { draw(bounds) }
            }
        }
    }
    Image(
        provider = ImageProvider(bitmap),
        contentDescription = null,
        modifier = GlanceModifier.size(size),
        colorFilter = ColorFilter.tint(tint),
    )
}

private const val ICON_DENSITY = 3f
