/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.presentation.ui.theme.PlanWidgetDimensions

@Composable
internal fun WidgetHeader(title: String) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetMark(GlanceModifier.size(PlanWidgetDimensions.BrandMarkSize))
        Spacer(GlanceModifier.width(4.dp))
        Text(
            text = title.uppercase(),
            maxLines = 1,
            style = TextStyle(
                color = PlanWidgetTheme.Secondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
internal fun WidgetPoster(
    title: String,
    poster: Bitmap?,
    width: Dp = PlanWidgetDimensions.CompactPosterWidth,
    height: Dp = PlanWidgetDimensions.CompactPosterHeight,
) {
    if (poster != null) {
        Image(
            provider = ImageProvider(poster),
            contentDescription = title,
            modifier = GlanceModifier
                .width(width)
                .height(height)
                .cornerRadius(PlanWidgetDimensions.PosterCornerSize),
            contentScale = ContentScale.Crop,
        )
    } else {
        WidgetMark(
            modifier = GlanceModifier
                .width(width)
                .height(height)
                .background(PlanWidgetTheme.Surface)
                .cornerRadius(PlanWidgetDimensions.PosterCornerSize),
        )
    }
}

@Composable
internal fun WidgetMiniPoster(title: String, poster: Bitmap?) {
    if (poster != null) {
        Image(
            provider = ImageProvider(poster),
            contentDescription = title,
            modifier = GlanceModifier
                .width(PlanWidgetDimensions.MiniPosterWidth)
                .height(PlanWidgetDimensions.MiniPosterHeight)
                .cornerRadius(PlanWidgetDimensions.MiniPosterCornerSize),
            contentScale = ContentScale.Crop,
        )
    } else {
        WidgetMark(
            modifier = GlanceModifier
                .width(PlanWidgetDimensions.MiniPosterWidth)
                .height(PlanWidgetDimensions.MiniPosterHeight)
                .background(PlanWidgetTheme.Surface)
                .cornerRadius(PlanWidgetDimensions.MiniPosterCornerSize),
        )
    }
}

@Composable
internal fun WidgetBackdrop(
    title: String,
    backdrop: Bitmap?,
    modifier: GlanceModifier = GlanceModifier.fillMaxWidth(),
    height: Dp = PlanWidgetDimensions.HeroHeight,
    fillHeight: Boolean = false,
    content: @Composable () -> Unit,
) {
    val sizedModifier = if (fillHeight) modifier else modifier.height(height)
    Box(
        modifier = sizedModifier
            .background(PlanWidgetTheme.ImageFallback)
            .cornerRadius(PlanWidgetDimensions.HeroCornerSize),
    ) {
        if (backdrop != null) {
            Image(
                provider = ImageProvider(backdrop),
                contentDescription = title,
                modifier = GlanceModifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            WidgetMark(GlanceModifier.fillMaxSize())
        }
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(PlanWidgetDimensions.HeroPadding),
            contentAlignment = Alignment.BottomStart,
        ) {
            content()
        }
    }
}

@Composable
internal fun EmptyWidgetMessage(context: Context, message: String, compact: Boolean) {
    if (compact) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WidgetMark(GlanceModifier.size(24.dp))
            Spacer(GlanceModifier.width(8.dp))
            Text(
                text = message,
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
            )
        }
    } else {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WidgetMark(GlanceModifier.size(26.dp))
            Text(
                text = message,
                maxLines = 1,
                style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
            )
            WidgetOpenAction(context.getString(R.string.widget_open_reels))
        }
    }
}

@Composable
private fun WidgetMark(modifier: GlanceModifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = GlanceModifier
                .size(PlanWidgetDimensions.BrandMarkSize)
                .background(PlanWidgetTheme.Accent)
                .cornerRadius(PlanWidgetDimensions.BrandMarkCornerSize),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "R",
                style = TextStyle(
                    color = PlanWidgetTheme.OnAccent,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

@Composable
internal fun WidgetDivider() {
    Spacer(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(PlanWidgetDimensions.DividerHeight)
            .background(PlanWidgetTheme.Divider),
    )
}

@Composable
internal fun WidgetCountdownBadge(label: String) {
    Box(
        modifier = GlanceModifier
            .background(PlanWidgetTheme.Surface)
            .cornerRadius(8.dp)
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            maxLines = 1,
            style = TextStyle(
                color = PlanWidgetTheme.Accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
private fun WidgetOpenAction(label: String) {
    Box(
        modifier = GlanceModifier
            .background(PlanWidgetTheme.Surface)
            .cornerRadius(12.dp)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = PlanWidgetTheme.Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}
