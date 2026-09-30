/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.list

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPosterRatingContainerColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun FeaturedMovieCarousel(
    movies: List<MovieDataModel>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val movieIds = remember(movies) { movies.map { movie -> movie.id } }
    var activeMovieIndex by remember(movieIds) { mutableIntStateOf(0) }

    LaunchedEffect(movieIds) {
        if (movieIds.size <= 1) return@LaunchedEffect

        while (isActive) {
            delay(HERO_AUTO_SCROLL_DELAY_MS)
            activeMovieIndex = (activeMovieIndex + 1) % movieIds.size
        }
    }

    Crossfade(
        targetState = activeMovieIndex,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal)
            .aspectRatio(ReelsDimensions.heroCardAspectRatio)
            .clip(MaterialTheme.shapes.extraLarge),
        animationSpec = tween(durationMillis = HERO_CROSSFADE_DURATION_MS),
        label = "Featured movie crossfade"
    ) { index ->
        val movie = movies.getOrNull(index) ?: movies.first()

        FeaturedMovieHero(
            movie = movie,
            activePage = index,
            pageCount = movies.size,
            onClick = {
                onMovieClick(movie.id)
            }
        )
    }
}

@Composable
private fun FeaturedMovieHero(
    movie: MovieDataModel,
    activePage: Int,
    pageCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = (movie.backdropPath ?: movie.posterPath).toTmdbImageUrl(
                imageType = TmdbImageType.Backdrop
            ),
            contentDescription = movie.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.18f),
                            Color.Black.copy(alpha = 0.82f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    horizontal = ReelsDimensions.heroContentHorizontalPadding,
                    vertical = ReelsDimensions.heroContentVerticalPadding
                ),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.heroContentSpacing)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBadge(rating = movie.voteAverage)

                movie.releaseDate.toReleaseYear()?.let { year ->
                    Text(
                        text = year,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.76f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                ),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = movie.overview.ifBlank { "No overview available yet." },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = Color.White.copy(alpha = 0.72f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            HeroPagerDots(
                activePage = activePage,
                pageCount = pageCount
            )
        }
    }
}

@Composable
internal fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = if (compact) {
            reelsPosterRatingContainerColor()
        } else {
            reelsImageRatingContainerColor()
        },
        contentColor = reelsImageRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) {
                    ReelsDimensions.compactRatingBadgeHorizontalPadding
                } else {
                    ReelsSpacing.medium
                },
                vertical = if (compact) {
                    ReelsDimensions.compactRatingBadgeVerticalPadding
                } else {
                    ReelsSpacing.xSmall
                }
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(
                    if (compact) ReelsDimensions.compactRatingBadgeIconSize else 14.dp
                )
            )

            Text(
                text = String.format("%.1f", rating),
                style = if (compact) {
                    MaterialTheme.typography.labelLarge.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                fontWeight = if (compact) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun HeroPagerDots(
    activePage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Surface(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(
                        width = if (index == activePage) {
                            ReelsDimensions.heroActiveDotWidth
                        } else {
                            ReelsDimensions.heroInactiveDotSize
                        },
                        height = ReelsDimensions.heroInactiveDotSize
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                color = if (index == activePage) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f)
                },
                content = {}
            )
        }
    }
}
