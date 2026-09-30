/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.model.favorite.SavedPersonDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbBlue
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbOnBlue
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsImageRatingContentColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPosterRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

@Composable
internal fun LibraryErrorState(
    listType: TmdbLibraryListType,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.xLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesEmptyContentSpacing)
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.favoritesEmptyIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.favoritesEmptyIconCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Sync,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.favoritesEmptyIconSize)
                )
            }
        }

        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = when (listType) {
                TmdbLibraryListType.Watchlist -> {
                    "Unable to load your watchlist. Check your connection and try again."
                }
                TmdbLibraryListType.Rated -> {
                    "Unable to load your ratings. Check your connection and try again."
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                modifier = Modifier.padding(start = ReelsSpacing.small),
                text = "Retry"
            )
        }
    }
}

@Composable
internal fun DisconnectedTmdbLibraryState(
    onConnectSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        LibraryTmdbLogoMark()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
        ) {
            Text(
                text = "Connect to TMDB",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Sign in to your TMDB account to sync your watchlist and ratings across devices.",
                modifier = Modifier.widthIn(
                    max = ReelsDimensions.favoritesDisconnectedBodyMaxWidth
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Surface(
            modifier = Modifier
                .widthIn(max = ReelsDimensions.favoritesDisconnectedContentMaxWidth)
                .fillMaxWidth(),
            shape = RoundedCornerShape(ReelsDimensions.favoritesFeatureCardCornerSize),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f))
        ) {
            Column {
                TmdbFeatureRow(
                    title = "Watchlist sync",
                    description = "Access movies you want to watch",
                    imageVector = Icons.Filled.Bookmark
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                )
                TmdbFeatureRow(
                    title = "Ratings sync",
                    description = "Your TMDB ratings in one place",
                    imageVector = Icons.Filled.Star
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                )
                TmdbFeatureRow(
                    title = "Offline access",
                    description = "Cache synced content locally",
                    imageVector = Icons.Filled.DownloadDone
                )
            }
        }

        Button(
            modifier = Modifier
                .widthIn(max = ReelsDimensions.favoritesConnectButtonMaxWidth)
                .fillMaxWidth(),
            onClick = onConnectSettingsClick
        ) {
            Icon(
                imageVector = Icons.Filled.Link,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Text(
                modifier = Modifier.padding(start = ReelsSpacing.small),
                text = "Connect in Settings",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LibraryTmdbLogoMark(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(ReelsDimensions.favoritesTmdbLogoSize),
        shape = MaterialTheme.shapes.large,
        color = ReelsTmdbBlue,
        contentColor = ReelsTmdbOnBlue
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.tmdb_logo),
                contentDescription = "TMDB logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ReelsSpacing.medium),
                tint = ReelsTmdbOnBlue
            )
        }
    }
}

@Composable
private fun TmdbFeatureRow(
    title: String,
    description: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsSpacing.large,
                vertical = ReelsSpacing.medium
            ),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.favoritesFeatureIconContainerSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.favoritesFeatureIconSize)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun EmptyLibraryState(
    selectedTab: LibraryTab,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.xLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesEmptyContentSpacing)
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.favoritesEmptyIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.favoritesEmptyIconCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (selectedTab) {
                        LibraryTab.Movies -> Icons.Outlined.FavoriteBorder
                        LibraryTab.People -> Icons.Filled.Person
                        LibraryTab.Tmdb -> Icons.Filled.Bookmark
                        LibraryTab.Rated -> Icons.Filled.Star
                    },
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.favoritesEmptyIconSize)
                )
            }
        }

        Text(
            text = when (selectedTab) {
                LibraryTab.Movies -> "No saved movies yet"
                LibraryTab.People -> "No saved people yet"
                LibraryTab.Tmdb -> "Your watchlist is empty"
                LibraryTab.Rated -> "No rated movies yet"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = when (selectedTab) {
                LibraryTab.Movies -> "Save movies from Movie Details to keep them available here and offline."
                LibraryTab.People -> "Save people from Cast Details to keep their profiles close at hand."
                LibraryTab.Tmdb -> "Add movies to your TMDB watchlist and they will appear here."
                LibraryTab.Rated -> "Rate movies from Movie Details to sync them with TMDB."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
