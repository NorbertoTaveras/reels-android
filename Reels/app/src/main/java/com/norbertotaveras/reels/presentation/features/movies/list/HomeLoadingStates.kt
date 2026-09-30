/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun SectionLoadingState(
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.posterRowSpacing)
    ) {
        items(SECTION_PLACEHOLDER_COUNT) {
            PosterPlaceholderCard()
        }
    }
}

@Composable
internal fun HomeLoadingState(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = ReelsDimensions.topPadding,
            bottom = ReelsSpacing.xxLarge
        ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.sectionSpacing)
    ) {
        item {
            HomeHeader()
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal)
                    .aspectRatio(ReelsDimensions.heroCardAspectRatio)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(reelsPlaceholderColor())
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            horizontal = ReelsDimensions.heroContentHorizontalPadding,
                            vertical = ReelsDimensions.heroContentVerticalPadding
                        ),
                    verticalArrangement = Arrangement.spacedBy(ReelsDimensions.heroContentSpacing)
                ) {
                    PlaceholderBar(width = 92.dp, height = 34.dp)
                    PlaceholderBar(width = 226.dp, height = 24.dp)
                    PlaceholderBar(width = 280.dp, height = 18.dp)
                    PlaceholderBar(width = 208.dp, height = 18.dp)
                }
            }
        }

        HOME_LOADING_SECTION_TITLES.forEach { title ->
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(ReelsDimensions.sectionHeaderToRowSpacing)
                ) {
                    HomeSectionHeader(
                        modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
                        title = title,
                        actionLabel = "See all",
                        onActionClick = {}
                    )

                    SectionLoadingState()
                }
            }
        }
    }
}

@Composable
private fun PosterPlaceholderCard(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(ReelsDimensions.posterCardWidth),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.posterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.large)
                .background(reelsPlaceholderColor())
        )

        PlaceholderBar(width = ReelsDimensions.posterCardWidth, height = 18.dp)
        PlaceholderBar(width = 64.dp, height = 16.dp)
    }
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
            .size(width = width, height = height)
            .clip(MaterialTheme.shapes.medium)
            .background(reelsPlaceholderColor())
    )
}
