/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.widget.shared

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
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
        modifier = GlanceModifier.fillMaxWidth().padding(
            start = PlanWidgetDimensions.ContentPadding,
            top = PlanWidgetDimensions.HeaderTopPadding,
            end = PlanWidgetDimensions.ContentPadding,
            bottom = PlanWidgetDimensions.HeaderBottomPadding,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetMark(GlanceModifier.size(PlanWidgetDimensions.BrandMarkSize))
        Spacer(GlanceModifier.width(PlanWidgetDimensions.HeaderItemSpacing))
        Text(
            text = title.uppercase(),
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1,
            style = TextStyle(
                color = PlanWidgetTheme.Secondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        WidgetIcon(Icons.Outlined.NorthEast, PlanWidgetDimensions.HeaderIconSize, PlanWidgetTheme.HeaderAction)
    }
}

@Composable
internal fun WidgetPoster(
    title: String,
    poster: Bitmap?,
    height: Dp = compactPosterHeight(),
    width: Dp = height * (PlanWidgetDimensions.CompactPosterWidth / PlanWidgetDimensions.CompactPosterHeight),
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
private fun compactPosterHeight(): Dp = minOf(
    PlanWidgetDimensions.CompactPosterHeight,
    (LocalSize.current.height - PlanWidgetDimensions.HeaderSpace -
        PlanWidgetDimensions.CompactTopPadding - PlanWidgetDimensions.CompactBottomPadding)
        .coerceAtLeast(0.dp),
)

@Composable
internal fun WidgetBackdrop(
    title: String,
    backdrop: Bitmap?,
    modifier: GlanceModifier = GlanceModifier.fillMaxWidth(),
    height: Dp = PlanWidgetDimensions.HeroHeight,
) {
    Box(
        modifier = modifier.height(height)
            .background(PlanWidgetTheme.ImageFallback),
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
    }
}

@Composable
internal fun EmptyWidgetMessage(context: Context, message: String, compact: Boolean) {
    Column(
        modifier = GlanceModifier.fillMaxWidth()
            .height((LocalSize.current.height - PlanWidgetDimensions.HeaderSpace).coerceAtLeast(0.dp))
            .padding(horizontal = PlanWidgetDimensions.EmptyContentPadding)
            .padding(
                top = if (compact) PlanWidgetDimensions.CompactTopPadding else PlanWidgetDimensions.EmptyContentPadding,
                bottom = if (compact) PlanWidgetDimensions.CompactBottomPadding else PlanWidgetDimensions.EmptyContentPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EmptyWidgetMark(compact)
        Spacer(GlanceModifier.height(if (compact) 4.dp else 8.dp))
        Text(
            text = message,
            maxLines = 1,
            style = TextStyle(
                color = PlanWidgetTheme.Secondary,
                fontSize = if (compact) 9.sp else 11.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
        if (!compact) {
            Spacer(GlanceModifier.height(PlanWidgetDimensions.ContentSpacing))
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
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(PlanWidgetDimensions.DividerHeight)
            .padding(horizontal = PlanWidgetDimensions.ContentPadding),
    ) {
        Spacer(GlanceModifier.fillMaxSize().background(PlanWidgetTheme.Divider))
    }
}

@Composable
internal fun WidgetCountdown(label: String) {
    Text(
        text = label,
        maxLines = 1,
        style = TextStyle(color = PlanWidgetTheme.Secondary, fontSize = 12.sp),
    )
}

@Composable
private fun WidgetOpenAction(label: String) {
    Box(
        modifier = GlanceModifier
            .background(ImageProvider(R.drawable.widget_open_action))
            .padding(
                horizontal = PlanWidgetDimensions.EmptyActionHorizontalPadding,
                vertical = PlanWidgetDimensions.EmptyActionVerticalPadding,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = PlanWidgetTheme.Accent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}
