/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.movie.model.MovieCategory
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.CachedContentBanner
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.components.common.InlineRetryState
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun HomeMovieSection(
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
                        message = "Showing saved movies",
                        supportingText = "New results will load when the connection improves"
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
internal fun HomeSectionHeader(
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
                fontSize = 18.sp,
                lineHeight = 27.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
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
                fontWeight = FontWeight.Normal
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
