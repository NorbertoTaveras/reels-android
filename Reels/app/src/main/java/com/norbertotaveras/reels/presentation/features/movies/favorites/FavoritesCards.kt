/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal

@Composable
internal fun TmdbAccountMovieCard(
    movie: MovieDataModel,
    listType: TmdbLibraryList,
    activityAtMillis: Long?,
    isUpdating: Boolean,
    onClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesTextSpacing)
    ) {
        LibraryPosterCard(
            imageUrl = movie.posterPath.toTmdbImageUrl(),
            contentDescription = movie.title,
            onClick = onClick,
            topStartBadge = {
                when (listType) {
                    TmdbLibraryList.Watchlist -> TmdbBadge()
                    TmdbLibraryList.Rated -> UserRatingBadge(
                        rating = movie.accountRating?.let { rating ->
                            String.format("%.1f", rating)
                        }.orEmpty()
                    )
                }
            },
            topEndBadge = {
                RatingBadge(
                    rating = String.format("%.1f", movie.voteAverage)
                )
            },
            bottomEndBadge = {
                when (listType) {
                    TmdbLibraryList.Watchlist -> LibraryOverlayIconBadge(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Remove from TMDB watchlist",
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        contentColor = ReelsTeal,
                        isLoading = isUpdating,
                        onClick = onActionClick
                    )

                    TmdbLibraryList.Rated -> LibraryOverlayIconBadge(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove TMDB rating",
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        isLoading = isUpdating,
                        onClick = onActionClick
                    )
                }
            },
            showBottomScrim = true
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            val releaseYear = movie.releaseDate.toReleaseYear()
            val activityLabel = activityAtMillis.toTmdbMovieActivityLabel(listType = listType)
            Text(
                text = buildAnnotatedString {
                    releaseYear?.let { year ->
                        append(year)
                        append(" · ")
                    }
                    withStyle(SpanStyle(color = ReelsTeal)) {
                        append(activityLabel)
                    }
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
