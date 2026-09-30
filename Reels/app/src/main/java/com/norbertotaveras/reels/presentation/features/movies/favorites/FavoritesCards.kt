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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContentColor

@Composable
internal fun TmdbWatchlistMovieCard(
    movie: MovieDataModel,
    listType: TmdbLibraryListType,
    onClick: () -> Unit,
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
                    TmdbLibraryListType.Watchlist -> TmdbBadge()
                    TmdbLibraryListType.Rated -> UserRatingBadge(
                        rating = movie.accountRating?.let { rating ->
                            String.format("%.1f", rating)
                        }.orEmpty()
                    )
                }
            },
            topEndBadge = {
                RatingBadge(
                    rating = when (listType) {
                        TmdbLibraryListType.Watchlist -> String.format("%.1f", movie.voteAverage)
                        TmdbLibraryListType.Rated -> String.format("%.1f", movie.voteAverage)
                    }
                )
            },
            bottomEndBadge = {
                when (listType) {
                    TmdbLibraryListType.Watchlist -> LibraryOverlayIconBadge(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Saved to TMDB watchlist",
                        containerColor = ReelsTeal.copy(alpha = 0.92f),
                        contentColor = MaterialTheme.colorScheme.background
                    )

                    TmdbLibraryListType.Rated -> LibraryOverlayIconBadge(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Rated on TMDB",
                        containerColor = reelsRatingContainerColor().copy(alpha = 0.92f),
                        contentColor = reelsRatingContentColor()
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

            Text(
                text = movie.releaseDate.toReleaseYear().orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
