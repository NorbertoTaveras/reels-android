/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.movie.model.MovieCategory
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredErrorState
import com.norbertotaveras.reels.presentation.ui.extensions.displayLabel
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun MoviesListContent(
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
                title = MovieCategory.POPULAR.displayLabel,
                category = MovieCategory.POPULAR,
                movies = popularMovies,
                onEvent = onEvent
            )
        }

        item {
            HomeMovieSection(
                title = MovieCategory.NOW_PLAYING.displayLabel,
                category = MovieCategory.NOW_PLAYING,
                movies = nowPlayingMovies,
                onEvent = onEvent
            )
        }

        item {
            HomeMovieSection(
                title = MovieCategory.UPCOMING.displayLabel,
                category = MovieCategory.UPCOMING,
                movies = upcomingMovies,
                onEvent = onEvent
            )
        }

        item {
            HomeMovieSection(
                title = MovieCategory.TOP_RATED.displayLabel,
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
                fontSize = 24.sp,
                lineHeight = 36.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Movies worth opening tonight",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
