/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.movie

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsMutedContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContentColor

@Composable
fun MovieResultRow(
    title: String,
    posterUrl: String?,
    modifier: Modifier = Modifier,
    releaseYear: String? = null,
    secondaryMetadata: String? = null,
    rating: String? = null,
    genres: List<String> = emptyList(),
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
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
            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                modifier = Modifier
                    .width(ReelsDimensions.searchResultPosterWidth)
                    .aspectRatio(ReelsRatios.poster)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!releaseYear.isNullOrBlank() || !secondaryMetadata.isNullOrBlank()) {
                    Spacer(
                        modifier = Modifier.height(
                            ReelsDimensions.searchResultTitleToMetadataSpacing
                        )
                    )
                    MovieResultMetadataRow(
                        modifier = Modifier.fillMaxWidth(),
                        releaseYear = releaseYear,
                        secondaryMetadata = secondaryMetadata
                    )
                }

                if (!rating.isNullOrBlank()) {
                    Spacer(
                        modifier = Modifier.height(
                            ReelsDimensions.searchResultMetadataToRatingSpacing
                        )
                    )
                    RatingPill(rating = rating)
                }

                if (genres.isNotEmpty()) {
                    Spacer(
                        modifier = Modifier.height(
                            ReelsDimensions.searchResultRatingToGenresSpacing
                        )
                    )
                    GenrePillRow(genres = genres)
                }
            }
        }
    }
}

@Composable
private fun MovieResultMetadataRow(
    releaseYear: String?,
    secondaryMetadata: String?,
    modifier: Modifier = Modifier,
) {
    val hasYear = !releaseYear.isNullOrBlank()
    val hasSecondaryMetadata = !secondaryMetadata.isNullOrBlank()

    if (!hasYear && !hasSecondaryMetadata) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            ReelsDimensions.searchResultMetadataContentSpacing
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasYear) {
            Text(
                text = releaseYear.orEmpty(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        if (hasYear && hasSecondaryMetadata) {
            Text(
                text = "·",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (hasSecondaryMetadata) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchResultMetadataIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = secondaryMetadata.orEmpty(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RatingPill(
    rating: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        border = BorderStroke(
            width = 1.dp,
            color = reelsRatingContentColor().copy(alpha = 0.20f)
        ),
        contentColor = reelsRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(
                ReelsDimensions.searchResultRatingContentSpacing
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.searchResultRatingIconSize)
            )

            Text(
                text = rating,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun GenrePillRow(
    genres: List<String>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultGenreSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultGenreSpacing)
    ) {
        genres.take(3).forEach { genre ->
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = reelsMutedContainerColor(),
                border = BorderStroke(
                    width = 1.dp,
                    color = reelsBorderColor()
                ),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = ReelsDimensions.searchResultGenreHorizontalPadding,
                        vertical = ReelsDimensions.searchResultGenreVerticalPadding
                    ),
                    text = genre,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        lineHeight = 12.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieResultRowPreview() {
    ReelsTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(ReelsSpacing.large)
        ) {
            MovieResultRow(
                title = "Dune: Part Two",
                posterUrl = null,
                releaseYear = "2024",
                secondaryMetadata = "2 years ago",
                rating = "8.7",
                genres = listOf("Sci-Fi", "Adventure"),
                onClick = {}
            )
        }
    }
}
