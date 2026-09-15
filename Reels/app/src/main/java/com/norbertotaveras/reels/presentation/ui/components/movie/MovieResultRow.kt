package com.norbertotaveras.reels.presentation.ui.components.movie

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
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
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ReelsDimensions.searchResultCardPadding),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
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
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = ReelsSpacing.xSmall),
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.searchResultContentSpacing),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 16.sp,
                        lineHeight = 21.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                MovieResultMetadataRow(
                    modifier = Modifier.fillMaxWidth(),
                    releaseYear = releaseYear,
                    secondaryMetadata = secondaryMetadata
                )

                if (!rating.isNullOrBlank()) {
                    RatingPill(rating = rating)
                }

                if (genres.isNotEmpty()) {
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
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasYear) {
            Text(
                text = releaseYear.orEmpty(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        if (hasYear && hasSecondaryMetadata) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (hasSecondaryMetadata) {
            Text(
                text = secondaryMetadata.orEmpty(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
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
        color = Color(0xFF493527),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsSpacing.small,
                vertical = ReelsSpacing.xSmall
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
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
                    fontSize = 14.sp,
                    lineHeight = 18.sp
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
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
    ) {
        genres.take(3).forEach { genre ->
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFF2F3035),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = ReelsSpacing.small,
                        vertical = ReelsSpacing.xSmall
                    ),
                    text = genre,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
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
