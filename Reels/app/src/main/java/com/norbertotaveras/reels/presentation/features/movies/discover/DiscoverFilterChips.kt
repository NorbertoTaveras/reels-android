/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiscoverChipSection(
    title: String,
    modifier: Modifier = Modifier,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        DiscoverSectionTitle(title = title)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.discoverFilterRowSpacing),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            maxItemsInEachRow = maxItemsInEachRow
        ) {
            content()
        }
    }
}

@Composable
internal fun DiscoverSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
internal fun DiscoverFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        } else {
            discoverFilterControlColor()
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
            }
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp,
                lineHeight = 18.sp
            ),
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun DiscoverFilterSkeletonChip(
    width: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .width(width)
            .height(36.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    )
}
