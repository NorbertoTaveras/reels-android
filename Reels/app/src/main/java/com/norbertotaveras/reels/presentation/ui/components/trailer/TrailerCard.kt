/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.trailer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

@Composable
fun TrailerCard(
    title: String,
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
    source: String? = null,
    duration: String? = null,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(ReelsDimensions.detailsTrailerWidth)
            .clickable(
                role = Role.Button,
                onClickLabel = "Play trailer: $title",
                onClick = onClick
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.trailer),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ReelsRatios.trailer)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ReelsRatios.trailer)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.30f))
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(ReelsDimensions.detailsTrailerPlayButtonSize),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.90f),
                    contentColor = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(ReelsDimensions.detailsTrailerPlayIconSize),
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                }

                if (!duration.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(ReelsSpacing.small),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = ReelsSpacing.small,
                                vertical = ReelsSpacing.xSmall
                            ),
                            text = duration,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (!source.isNullOrBlank()) {
            Text(
                text = source,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrailerCardPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(ReelsSpacing.large)
        ) {
            TrailerCard(
                title = "Official Trailer",
                thumbnailUrl = null,
                source = "YouTube",
                duration = "3:12",
                onClick = {}
            )
        }
    }
}
