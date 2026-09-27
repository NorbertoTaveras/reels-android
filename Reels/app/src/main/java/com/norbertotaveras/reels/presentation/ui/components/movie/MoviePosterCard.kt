/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatingContent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor

@Composable
fun MoviePosterCard(
    title: String,
    posterUrl: String?,
    modifier: Modifier = Modifier,
    releaseYear: String? = null,
    rating: String? = null,
    width: Dp = 120.dp,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    releaseYearStyle: TextStyle = MaterialTheme.typography.labelMedium,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(width)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ReelsRatios.poster)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )

                if (!rating.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(ReelsSpacing.small),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = reelsRatingContainerColor(),
                        contentColor = ReelsRatingContent
                    ) {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = ReelsSpacing.small,
                                vertical = ReelsSpacing.xSmall
                            ),
                            text = "★ $rating",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        Text(
            text = title,
            style = titleStyle,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (!releaseYear.isNullOrBlank()) {
            Text(
                text = releaseYear,
                style = releaseYearStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoviePosterCardPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(ReelsSpacing.large)
        ) {
            MoviePosterCard(
                title = "Dune: Part Two",
                posterUrl = null,
                releaseYear = "2024",
                rating = "8.4",
                onClick = {}
            )
        }
    }
}
