/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun FavoriteMovieCard(
    movie: FavoriteMovieDataModel,
    isRemoving: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
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
                OfflineBadge()
            },
            topEndBadge = {
                RatingBadge(rating = String.format("%.1f", movie.voteAverage))
            },
            bottomEndBadge = {
                LibraryRemoveButton(
                    isRemoving = isRemoving,
                    contentDescription = "Remove ${movie.title} from library",
                    onClick = onFavoriteClick
                )
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
