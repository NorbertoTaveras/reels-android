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
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPosterRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun MoviesListContent(
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
internal fun HomeHeader(
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
