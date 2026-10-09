/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun SearchLoadingHeader(
    modifier: Modifier = Modifier,
) {
    SearchPlaceholder(
        modifier = modifier.padding(bottom = ReelsSpacing.small),
        width = ReelsDimensions.searchLoadingHeaderWidth,
        height = ReelsDimensions.searchLoadingHeaderHeight
    )
}

@Composable
internal fun SearchResultPlaceholder(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = ReelsDimensions.searchResultBorderWidth,
            color = reelsBorderColor()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ReelsDimensions.searchResultCardPadding),
            horizontalArrangement = Arrangement.spacedBy(
                ReelsDimensions.searchResultCardContentSpacing
            ),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(
                        width = ReelsDimensions.searchResultPosterWidth,
                        height = ReelsDimensions.searchResultPosterHeight
                    )
                    .clip(MaterialTheme.shapes.medium)
                    .background(reelsPlaceholderColor())
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
            ) {
                SearchPlaceholder(
                    width = ReelsDimensions.searchLoadingTitleWidth,
                    height = ReelsDimensions.searchLoadingTitleHeight
                )
                SearchPlaceholder(
                    width = ReelsDimensions.searchLoadingMetadataWidth,
                    height = ReelsDimensions.searchLoadingMetadataHeight
                )
                SearchPlaceholder(
                    width = ReelsDimensions.searchLoadingRatingWidth,
                    height = ReelsDimensions.searchLoadingRatingHeight,
                    cornerRadius = ReelsSpacing.small
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        ReelsDimensions.searchResultGenreSpacing
                    )
                ) {
                    repeat(3) {
                        SearchPlaceholder(
                            width = ReelsDimensions.searchLoadingGenreWidth,
                            height = ReelsDimensions.searchLoadingGenreHeight,
                            cornerRadius = ReelsSpacing.medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchPlaceholder(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = ReelsSpacing.xSmall,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(reelsPlaceholderColor())
    )
}
