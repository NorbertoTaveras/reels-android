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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.CachedContentBanner
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredErrorState
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatingContent
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun MoviesListScreen(
    state: MoviesListState,
    popularMovies: LazyPagingItems<MovieDataModel>,
    nowPlayingMovies: LazyPagingItems<MovieDataModel>,
    upcomingMovies: LazyPagingItems<MovieDataModel>,
    topRatedMovies: LazyPagingItems<MovieDataModel>,
    onEvent: (MoviesListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val popularRefreshState = popularMovies.loadState.refresh

    when {
        popularRefreshState is LoadState.Loading && popularMovies.itemCount == 0 -> {
            HomeLoadingState(modifier = modifier)
            return
        }

        popularRefreshState is LoadState.Error && popularMovies.itemCount == 0 -> {
            val message = popularRefreshState.error.toUserMessage(
                fallback = "Unable to load movies right now."
            )
            CenteredErrorState(
                modifier = modifier,
                title = message.toErrorTitle(fallback = "Unable to load movies"),
                message = message,
                retryLabel = "Try Again",
                onRetry = popularMovies::retry
            )
            return
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = ReelsDimensions.topPadding,
            bottom = ReelsDimensions.homeScreenBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.sectionSpacing)
    ) {
        item {
            HomeHeader()
        }

        item {
            val featuredMovies = popularMovies.itemSnapshotList.items.take(HERO_MOVIE_LIMIT)
            if (featuredMovies.isNotEmpty()) {
                FeaturedMovieCarousel(
                    movies = featuredMovies,
                    onMovieClick = { movieId ->
                        onEvent(MoviesListEvent.MovieClicked(movieId = movieId))
                    }
                )
            }
        }

        item {
            HomeMovieSection(
                title = MovieCategory.POPULAR.label,
                category = MovieCategory.POPULAR,
                movies = popularMovies,
                onEvent = onEvent
            )
        }

        item {
            HomeMovieSection(
                title = MovieCategory.NOW_PLAYING.label,
                category = MovieCategory.NOW_PLAYING,
                movies = nowPlayingMovies,
                onEvent = onEvent
            )
        }

        item {
            HomeMovieSection(
                title = MovieCategory.UPCOMING.label,
                category = MovieCategory.UPCOMING,
                movies = upcomingMovies,
                onEvent = onEvent
            )
        }

        item {
            HomeMovieSection(
                title = MovieCategory.TOP_RATED.label,
                category = MovieCategory.TOP_RATED,
                movies = topRatedMovies,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun HomeHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.headerTextSpacing)
    ) {
        Text(
            text = "Reels",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                lineHeight = 28.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Movies worth opening tonight",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 22.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeaturedMovieCarousel(
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
private fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = reelsRatingContainerColor(),
        contentColor = ReelsRatingContent
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
                    ReelsSpacing.small
                }
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(
                    if (compact) ReelsDimensions.compactRatingBadgeIconSize else 16.dp
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
                    MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        lineHeight = 22.sp
                    )
                },
                fontWeight = FontWeight.Bold
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

@Composable
private fun HomeMovieSection(
    title: String,
    category: MovieCategory,
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (MoviesListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.sectionHeaderToRowSpacing)
    ) {
        HomeSectionHeader(
            modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
            title = title,
            actionLabel = "See all",
            onActionClick = {
                onEvent(MoviesListEvent.SeeAllClicked(category = category))
            }
        )

        val refreshState = movies.loadState.refresh
        val cachedRefreshError = movies.loadState.mediator?.refresh as? LoadState.Error
        val hasCachedMovies = movies.itemCount > 0

        when {
            refreshState is LoadState.Loading && !hasCachedMovies -> SectionLoadingState()

            refreshState is LoadState.Error && !hasCachedMovies -> {
                InlineRetryState(
                    modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
                    message = refreshState.error.toUserMessage(
                        fallback = "Unable to load $title."
                    ),
                    retryLabel = "Retry",
                    onRetry = movies::retry
                )
            }

            !hasCachedMovies -> {
                EmptyState(
                    modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
                    title = "No movies found",
                    message = "Try refreshing this section again in a moment."
                )
            }

            else -> {
                if (cachedRefreshError != null || refreshState is LoadState.Error) {
                    CachedContentBanner(
                        modifier = Modifier.padding(horizontal = ReelsSpacing.screenHorizontal),
                        message = "Showing saved movies. New results will load when the connection improves."
                    )
                }

                MoviePosterRow(
                    movies = movies,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun HomeSectionHeader(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                lineHeight = 26.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.clickable(onClick = onActionClick),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SectionLoadingState(
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
private fun HomeLoadingState(
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
                    .background(placeholderColor())
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
                .background(placeholderColor())
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
            .background(placeholderColor())
    )
}

@Composable
private fun placeholderColor(): Color {
    return MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f)
}

@Composable
private fun MoviePosterRow(
    movies: LazyPagingItems<MovieDataModel>,
    onEvent: (MoviesListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.posterRowSpacing)
    ) {
        items(
            count = minOf(movies.itemCount, HOME_SECTION_LIMIT),
            key = { index ->
                val movie = movies.peek(index)
                "${movie?.id ?: "placeholder"}-$index"
            }
        ) { index ->
            val movie = movies[index] ?: return@items

            HomePosterCard(
                movie = movie,
                onClick = {
                    onEvent(MoviesListEvent.MovieClicked(movieId = movie.id))
                }
            )
        }

        when (val appendState = movies.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .padding(top = 72.dp)
                            .size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    InlineRetryState(
                        modifier = Modifier
                            .padding(top = ReelsSpacing.xLarge)
                            .size(width = 160.dp, height = 160.dp),
                        message = appendState.error.toUserMessage(
                            fallback = "Unable to load more movies."
                        ),
                        retryLabel = "Retry",
                        onRetry = movies::retry
                    )
                }
            }

            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun HomePosterCard(
    movie: MovieDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(ReelsDimensions.posterCardWidth)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.posterTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(MaterialTheme.shapes.large)
        ) {
            AsyncImage(
                model = movie.posterPath.toTmdbImageUrl(),
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            RatingBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(ReelsDimensions.compactRatingBadgeEdgePadding),
                rating = movie.voteAverage,
                compact = true
            )
        }

        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 21.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        movie.releaseDate.toReleaseYear()?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private const val HERO_MOVIE_LIMIT = 3
private const val HERO_AUTO_SCROLL_DELAY_MS = 2_800L
private const val HERO_CROSSFADE_DURATION_MS = 650
private const val HOME_SECTION_LIMIT = 10
private const val SECTION_PLACEHOLDER_COUNT = 4
private val HOME_LOADING_SECTION_TITLES = listOf(
    MovieCategory.POPULAR.label,
    MovieCategory.NOW_PLAYING.label,
    MovieCategory.UPCOMING.label,
    MovieCategory.TOP_RATED.label
)
