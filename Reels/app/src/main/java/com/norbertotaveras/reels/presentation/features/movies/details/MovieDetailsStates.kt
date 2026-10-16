/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.components.common.MessageState
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun MovieDetailsLoadingState(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = ReelsSpacing.xxLarge),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsSectionSpacing)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ReelsDimensions.detailsLoadingHeroHeight)
                    .background(reelsPlaceholderColor())
            ) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = ReelsSpacing.screenHorizontal,
                            top = ReelsDimensions.detailsHeroTopActionPadding
                        )
                        .size(ReelsDimensions.detailsBackButtonSize),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }

                PlaceholderBox(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = ReelsSpacing.screenHorizontal, bottom = ReelsSpacing.large)
                        .width(ReelsDimensions.detailsPosterWidth)
                        .aspectRatio(ReelsRatios.poster)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = ReelsSpacing.screenHorizontal +
                                ReelsDimensions.detailsPosterWidth +
                                ReelsSpacing.medium,
                            end = ReelsSpacing.screenHorizontal,
                            bottom = ReelsSpacing.large
                        ),
                    verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
                ) {
                    PlaceholderBar(width = 180.dp, height = 22.dp)
                    PlaceholderBar(width = 156.dp, height = 16.dp)
                    Row(horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
                        PlaceholderBar(width = 72.dp, height = 28.dp)
                        PlaceholderBar(width = 52.dp, height = 28.dp)
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
                PlaceholderBar(width = 118.dp, height = 24.dp)
                PlaceholderBar(width = 280.dp, height = 18.dp)
                PlaceholderBar(width = 310.dp, height = 18.dp)
                PlaceholderBar(width = 230.dp, height = 18.dp)
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                repeat(3) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlaceholderBox(
                            modifier = Modifier.size(ReelsDimensions.detailsInfoIconContainerSize)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                            PlaceholderBar(width = 96.dp, height = 14.dp)
                            PlaceholderBar(width = 132.dp, height = 16.dp)
                        }
                    }
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                PlaceholderBar(
                    modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
                    width = ReelsDimensions.detailsLoadingCastHeaderWidth,
                    height = ReelsDimensions.detailsLoadingCastHeaderHeight
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
                    horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
                ) {
                    items(5) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(ReelsDimensions.detailsLoadingCastAvatarSize)
                                    .clip(CircleShape)
                                    .background(reelsPlaceholderColor())
                            )
                            PlaceholderBar(
                                modifier = Modifier.padding(
                                    top = ReelsDimensions
                                        .detailsLoadingCastAvatarToNameSpacing
                                ),
                                width = ReelsDimensions.detailsLoadingCastNameWidth,
                                height = ReelsDimensions.detailsLoadingCastTextHeight
                            )
                            PlaceholderBar(
                                modifier = Modifier.padding(
                                    top = ReelsDimensions.detailsLoadingCastNameToRoleSpacing
                                ),
                                width = ReelsDimensions.detailsLoadingCastRoleWidth,
                                height = ReelsDimensions.detailsLoadingCastTextHeight
                            )
                        }
                    }
                }
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                items(2) {
                    PlaceholderBox(
                        modifier = Modifier
                            .width(ReelsDimensions.detailsTrailerWidth)
                            .height(108.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun MovieDetailsErrorState(
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
                top = ReelsSpacing.large,
                end = ReelsSpacing.screenHorizontal,
                bottom = ReelsSpacing.xxLarge
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsSectionSpacing)
    ) {
        MovieDetailsStateHeader(
            title = "Movie Details",
            onBackClick = onBackClick
        )

        MessageState(
            icon = Icons.Outlined.ErrorOutline,
            title = message.toErrorTitle(fallback = "Unable to load movie"),
            message = message,
            actionLabel = retryLabel,
            actionIcon = Icons.Outlined.Refresh,
            onAction = onRetry
        )
    }
}

@Composable
private fun MovieDetailsStateHeader(
    title: String,
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
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
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
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 30.sp,
                lineHeight = 36.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PlaceholderBox(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(reelsPlaceholderColor())
    )
}

@Composable
private fun PlaceholderBar(
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
