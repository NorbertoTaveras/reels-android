/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.category

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContentColor

@Composable
fun MovieCategoryScreen(
    state: MovieCategoryUiState,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (MovieCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val refreshState = movies.loadState.refresh) {
        is LoadState.Loading -> {
            MovieCategoryLoadingState(
                modifier = modifier,
                category = state.category,
                onBackClick = {
                    onEvent(MovieCategoryEvent.BackClicked)
                }
            )
            return
        }

        is LoadState.Error -> {
            MovieCategoryErrorState(
                modifier = modifier,
                category = state.category,
                message = refreshState.error.toUserMessage(
                    fallback = "Unable to load ${state.category.label} movies."
                ),
                retryLabel = "Try Again",
                onBackClick = {
                    onEvent(MovieCategoryEvent.BackClicked)
                },
                onRetry = movies::retry,
            )
            return
        }

        is LoadState.NotLoading -> Unit
    }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            start = ReelsDimensions.categoryScreenHorizontalPadding,
            top = ReelsDimensions.categoryScreenTopPadding,
            end = ReelsDimensions.categoryScreenHorizontalPadding,
            bottom = ReelsDimensions.categoryScreenBottomPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridHorizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridVerticalSpacing)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            MovieCategoryHeader(
                category = state.category,
                movieCount = movies.itemCount,
                hasMoreMovies = movies.loadState.append.let { loadState ->
                    loadState !is LoadState.NotLoading || !loadState.endOfPaginationReached
                },
                onBackClick = {
                    onEvent(MovieCategoryEvent.BackClicked)
                }
            )
        }

        if (movies.itemCount == 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    title = "No movies found",
                    message = "Try refreshing this section again in a moment."
                )
            }
        } else {
            items(
                count = movies.itemCount,
                key = { index ->
                    val movie = movies.peek(index)
                    "${movie?.id ?: "placeholder"}-$index"
                }
            ) { index ->
                val movie = movies[index] ?: return@items

                MovieCategoryPosterCard(
                    movie = movie,
                    onClick = {
                        onEvent(MovieCategoryEvent.MovieClicked(movieId = movie.id))
                    }
                )
            }
        }

        when (val appendState = movies.loadState.append) {
            is LoadState.Loading -> {
                items(CATEGORY_APPEND_PLACEHOLDER_COUNT) {
                    MovieCategoryPlaceholderCard()
                }
            }

            is LoadState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    InlineRetryState(
                        message = appendState.error.toUserMessage(
                            fallback = "Unable to load more movies."
                        ),
                        retryLabel = "Retry",
                        onRetry = movies::retry
                    )
                }
            }

            is LoadState.NotLoading -> {
                if (appendState.endOfPaginationReached && movies.itemCount > 0) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        MovieCategoryEndCap(movieCount = movies.itemCount)
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieCategoryHeader(
    category: MovieCategory,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    movieCount: Int? = null,
    hasMoreMovies: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = ReelsDimensions.categoryHeaderBottomPadding),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryHeaderContentSpacing)
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.categoryBackButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
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

        Column(
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryHeaderTextSpacing)
        ) {
            Text(
                text = category.label,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    lineHeight = 30.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = category.supportingText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            movieCount?.takeIf { count -> count > 0 }?.let { count ->
                MovieCategoryCountChip(
                    modifier = Modifier.padding(top = ReelsDimensions.categoryHeaderCountSpacing),
                    movieCount = count,
                    hasMoreMovies = hasMoreMovies
                )
            }
        }
    }
}

@Composable
private fun MovieCategoryCountChip(
    movieCount: Int,
    hasMoreMovies: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.categoryCountHorizontalPadding,
                vertical = ReelsDimensions.categoryCountVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Movie,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.categoryCountIconSize)
            )

            Text(
                text = if (hasMoreMovies) "$movieCount+ movies" else "$movieCount movies",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            )
        }
    }
}

@Composable
private fun MovieCategoryLoadingState(
    category: MovieCategory,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            start = ReelsDimensions.categoryScreenHorizontalPadding,
            top = ReelsDimensions.categoryScreenTopPadding,
            end = ReelsDimensions.categoryScreenHorizontalPadding,
            bottom = ReelsDimensions.categoryScreenBottomPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridHorizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridVerticalSpacing)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            MovieCategoryHeader(
                category = category,
                onBackClick = onBackClick
            )
        }

        items(CATEGORY_PLACEHOLDER_COUNT) {
            MovieCategoryPlaceholderCard()
        }
    }
}

@Composable
private fun MovieCategoryErrorState(
    category: MovieCategory,
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
                start = ReelsDimensions.categoryScreenHorizontalPadding,
                top = ReelsDimensions.categoryScreenTopPadding,
                end = ReelsDimensions.categoryScreenHorizontalPadding,
                bottom = ReelsDimensions.categoryScreenBottomPadding
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.sectionSpacing)
    ) {
        MovieCategoryHeader(
            category = category,
            onBackClick = onBackClick
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
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
                    text = message.toErrorTitle(
                        fallback = "Unable to load ${category.label.lowercase()}"
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
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

@Composable
private fun MovieCategoryPlaceholderCard(
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryPosterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.medium)
                .background(categoryPlaceholderColor())
        )

        CategoryPlaceholderBar(widthFraction = 0.92f, height = 12.dp)
        CategoryPlaceholderBar(widthFraction = 0.45f, height = 10.dp)
    }
}

@Composable
private fun CategoryPlaceholderBar(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(MaterialTheme.shapes.medium)
            .background(categoryPlaceholderColor())
    )
}

@Composable
private fun categoryPlaceholderColor(): Color {
    return MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f)
}

@Composable
private fun MovieCategoryPosterCard(
    movie: MovieDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryPosterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = movie.posterPath.toTmdbImageUrl(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            CategoryRatingBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(ReelsDimensions.categoryRatingEdgePadding),
                rating = movie.voteAverage
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.releaseDate.toReleaseYear()?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CategoryRatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = reelsRatingContainerColor(),
        contentColor = reelsRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.categoryRatingHorizontalPadding,
                vertical = ReelsDimensions.categoryRatingVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.categoryRatingIconSize)
            )

            Text(
                text = String.format("%.1f", rating),
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
private fun MovieCategoryEndCap(
    movieCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        Box(
            modifier = Modifier
                .size(width = 32.dp, height = 1.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))
        )

        Text(
            text = "All $movieCount movies loaded",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val MovieCategory.supportingText: String
    get() = when (this) {
        MovieCategory.POPULAR -> "What everyone is watching right now"
        MovieCategory.NOW_PLAYING -> "Movies playing in theaters now"
        MovieCategory.UPCOMING -> "Movies arriving in theaters soon"
        MovieCategory.TOP_RATED -> "The highest-rated movies on TMDB"
    }

private const val CATEGORY_PLACEHOLDER_COUNT = 12
private const val CATEGORY_APPEND_PLACEHOLDER_COUNT = 3
