/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.components.common.MessageState
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun CastDetailsLoadingState(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = ReelsSpacing.xxLarge),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.section)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ReelsDimensions.castDetailsHeroHeight)
                    .background(reelsPlaceholderColor())
            ) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = ReelsSpacing.screenHorizontal,
                            top = ReelsDimensions.castDetailsHeroActionTopPadding
                        )
                        .size(ReelsDimensions.castDetailsHeroActionButtonSize),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(ReelsDimensions.castDetailsHeroActionIconSize)
                        )
                    }
                }

                CastDetailsPlaceholderBar(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = ReelsSpacing.screenHorizontal, bottom = 36.dp),
                    width = 220.dp,
                    height = 28.dp
                )

                CastDetailsPlaceholderBar(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = ReelsSpacing.screenHorizontal, bottom = 4.dp),
                    width = 92.dp,
                    height = 16.dp
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.castDetailsQuickInfoSpacing)
            ) {
                repeat(2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CastDetailsPlaceholderBox(
                            modifier = Modifier.size(ReelsDimensions.castDetailsInfoIconContainerSize)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                            CastDetailsPlaceholderBar(width = 82.dp, height = 14.dp)
                            CastDetailsPlaceholderBar(width = 190.dp, height = 18.dp)
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                CastDetailsPlaceholderBar(width = 116.dp, height = 24.dp)
                CastDetailsPlaceholderBar(width = 308.dp, height = 18.dp)
                CastDetailsPlaceholderBar(width = 294.dp, height = 18.dp)
                CastDetailsPlaceholderBar(width = 236.dp, height = 18.dp)
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                items(CAST_DETAILS_KNOWN_FOR_PLACEHOLDER_COUNT) {
                    Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
                        CastDetailsPlaceholderBox(
                            modifier = Modifier
                                .width(ReelsDimensions.posterCardWidth)
                                .height(178.dp)
                        )
                        CastDetailsPlaceholderBar(width = 106.dp, height = 16.dp)
                    }
                }
            }
        }
    }
}

@Composable
internal fun CastDetailsErrorState(
    message: String,
    retryLabel: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = ReelsSpacing.screenHorizontal,
                top = ReelsDimensions.fullCastScreenTopPadding,
                end = ReelsSpacing.screenHorizontal,
                bottom = ReelsSpacing.xxLarge
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsSectionSpacing)
    ) {
        CastDetailsStateHeader(onBackClick = onBackClick)

        MessageState(
            icon = Icons.Outlined.ErrorOutline,
            title = message.toErrorTitle(fallback = "Unable to load cast member"),
            message = message,
            actionLabel = retryLabel,
            actionIcon = Icons.Outlined.Refresh,
            onAction = onRetry
        )
    }
}

@Composable
private fun CastDetailsStateHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.categoryBackButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(ReelsDimensions.categoryBackIconSize)
                )
            }
        }

        Text(
            text = "Cast Details",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CastDetailsPlaceholderBox(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(reelsPlaceholderColor())
    )
}

@Composable
private fun CastDetailsPlaceholderBar(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(MaterialTheme.shapes.medium)
            .background(reelsPlaceholderColor())
    )
}
