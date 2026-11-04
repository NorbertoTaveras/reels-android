/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.person.model.PersonMovieCreditDataModel
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarousel
import com.norbertotaveras.reels.presentation.ui.components.movie.PosterCarouselItem
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun KnownForPreviewSection(
    knownFor: List<PersonMovieCreditDataModel>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (knownFor.isEmpty()) return
    val previewMovies = knownFor.take(KNOWN_FOR_PREVIEW_COUNT)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
            text = "Known For",
            style = castDetailsSectionTitleStyle(),
            color = MaterialTheme.colorScheme.onBackground
        )

        PosterCarousel(
            items = previewMovies.map { movie ->
                PosterCarouselItem(
                    id = movie.id,
                    title = movie.title,
                    posterUrl = movie.posterPath.toTmdbImageUrl()
                )
            },
            onMovieClick = onMovieClick,
            contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
            cardWidth = ReelsDimensions.castDetailsKnownForPosterWidth,
            titleStyle = MaterialTheme.typography.bodyMedium,
            posterShape = RoundedCornerShape(ReelsDimensions.castDetailsCardCornerRadius)
        )
    }
}

private const val KNOWN_FOR_PREVIEW_COUNT = 3


@Composable
internal fun FullFilmographySection(
    creditCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .padding(horizontal = ReelsSpacing.screenHorizontal)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(ReelsDimensions.castDetailsCardCornerRadius),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(ReelsSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.castDetailsInfoIconContainerSize),
                shape = RoundedCornerShape(ReelsDimensions.castDetailsInfoCornerRadius),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Movie,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.castDetailsFilmographyIconSize)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Full Filmography",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$creditCount credits",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.castDetailsFilmographyChevronSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
