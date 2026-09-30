/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.cast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun MovieCastLoadingState(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = ReelsSpacing.screenHorizontal,
            top = ReelsDimensions.fullCastScreenTopPadding,
            end = ReelsSpacing.screenHorizontal,
            bottom = ReelsDimensions.fullCastScreenBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.fullCastRowSpacing)
    ) {
        item {
            MovieCastHeader(
                modifier = Modifier.padding(bottom = ReelsDimensions.fullCastHeaderBottomPadding),
                onBackClick = onBackClick
            )
        }

        items(FULL_CAST_PLACEHOLDER_COUNT) {
            MovieCastPlaceholderRow()
        }
    }
}

@Composable
internal fun MovieCastErrorState(
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
                bottom = ReelsDimensions.fullCastScreenBottomPadding
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsSectionSpacing)
    ) {
        MovieCastHeader(onBackClick = onBackClick)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = ReelsDimensions.fullCastBorderWidth,
                color = MaterialTheme.colorScheme.onSurface
                    .copy(alpha = 0.12f)
            )
        ) {
            Column(
                modifier = Modifier.padding(ReelsDimensions.stateCardPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.stateCardContentSpacing)
            ) {
                Surface(
                    modifier = Modifier.size(ReelsDimensions.stateIconContainerSize),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(ReelsDimensions.stateIconSize)
                        )
                    }
                }

                Text(
                    text = message.toErrorTitle(fallback = "Unable to load cast"),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                FilledTonalButton(
                    modifier = Modifier.padding(top = ReelsSpacing.small),
                    onClick = onRetry
                ) {
                    Text(text = retryLabel)
                }
            }
        }
    }
}
