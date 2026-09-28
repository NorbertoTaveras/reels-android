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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
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

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    tmdbWatchlistMovies: LazyPagingItems<MovieDataModel>,
    tmdbRatedMovies: LazyPagingItems<MovieDataModel>,
    onEvent: (FavoritesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = ReelsSpacing.screenHorizontal,
            top = ReelsDimensions.favoritesScreenTopPadding,
            end = ReelsSpacing.screenHorizontal,
            bottom = ReelsDimensions.favoritesScreenBottomPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesGridHorizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesGridVerticalSpacing)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            LibraryHeader(
                state = state,
                onTabSelected = { tab ->
                    onEvent(FavoritesEvent.TabSelected(tab = tab))
                }
            )
        }

        if (!state.hasSelectedTabItems) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyLibraryState(
                    selectedTab = state.selectedTab,
                    modifier = Modifier.padding(top = ReelsDimensions.favoritesEmptyTopPadding)
                )
            }
        } else {
            when (state.selectedTab) {
                LibraryTab.Movies -> {
                    items(
                        items = state.movies,
                        key = { movie -> movie.id }
                    ) { movie ->
                        FavoriteMovieCard(
                            movie = movie,
                            isRemoving = state.removingMovieIds.contains(movie.id),
                            onClick = {
                                onEvent(FavoritesEvent.MovieClicked(movieId = movie.id))
                            },
                            onFavoriteClick = {
                                onEvent(FavoritesEvent.FavoriteClicked(movieId = movie.id))
                            }
                        )
                    }
                }

                LibraryTab.People -> {
                    items(
                        items = state.people,
                        key = { person -> person.id }
                    ) { person ->
                        SavedPersonCard(
                            person = person,
                            isRemoving = state.removingPersonIds.contains(person.id),
                            onClick = {
                                onEvent(FavoritesEvent.PersonClicked(personId = person.id))
                            },
                            onFavoriteClick = {
                                onEvent(FavoritesEvent.SavedPersonClicked(personId = person.id))
                            }
                        )
                    }
                }

                LibraryTab.Tmdb -> {
                    tmdbAccountMovieItems(
                        state = state,
                        movies = tmdbWatchlistMovies,
                        listType = TmdbLibraryListType.Watchlist,
                        onConnectSettingsClick = {
                            onEvent(FavoritesEvent.ConnectSettingsClicked)
                        },
                        onMovieClick = { movieId ->
                            onEvent(FavoritesEvent.MovieClicked(movieId = movieId))
                        },
                        onRefresh = tmdbWatchlistMovies::refresh
                    )
                }

                LibraryTab.Rated -> {
                    tmdbAccountMovieItems(
                        state = state,
                        movies = tmdbRatedMovies,
                        listType = TmdbLibraryListType.Rated,
                        onConnectSettingsClick = {
                            onEvent(FavoritesEvent.ConnectSettingsClicked)
                        },
                        onMovieClick = { movieId ->
                            onEvent(FavoritesEvent.MovieClicked(movieId = movieId))
                        },
                        onRefresh = tmdbRatedMovies::refresh
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryHeader(
    state: FavoritesUiState,
    onTabSelected: (LibraryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesHeaderToBadgeSpacing)
    ) {
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                lineHeight = 30.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = state.librarySubtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LibraryTabs(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ReelsSpacing.medium),
            selectedTab = state.selectedTab,
            onTabSelected = onTabSelected
        )
    }
}

private enum class TmdbLibraryListType {
    Watchlist,
    Rated
}

private val TmdbLibraryListType.emptyTab: LibraryTab
    get() = when (this) {
        TmdbLibraryListType.Watchlist -> LibraryTab.Tmdb
        TmdbLibraryListType.Rated -> LibraryTab.Rated
    }

private fun androidx.compose.foundation.lazy.grid.LazyGridScope.tmdbAccountMovieItems(
    state: FavoritesUiState,
    movies: LazyPagingItems<MovieDataModel>,
    listType: TmdbLibraryListType,
    onConnectSettingsClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onRefresh: () -> Unit,
) {
    when {
        !state.isTmdbAccountConnected -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DisconnectedTmdbLibraryState(
                    onConnectSettingsClick = onConnectSettingsClick,
                    modifier = Modifier.padding(top = ReelsDimensions.favoritesEmptyTopPadding)
                )
            }
        }

        movies.loadState.refresh is LoadState.Loading -> {
            repeat(LIBRARY_LOADING_PLACEHOLDER_COUNT) { index ->
                item(key = "${listType.name}-loading-$index") {
                    LibraryPosterPlaceholder()
                }
            }
        }

        movies.loadState.refresh is LoadState.Error -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LibraryErrorState(
                    listType = listType,
                    onRetry = onRefresh,
                    modifier = Modifier.padding(top = ReelsDimensions.favoritesEmptyTopPadding)
                )
            }
        }

        movies.itemCount == 0 -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyLibraryState(
                    selectedTab = listType.emptyTab,
                    modifier = Modifier.padding(top = ReelsDimensions.favoritesEmptyTopPadding)
                )
            }
        }

        else -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LibrarySyncStatus(
                    listType = listType,
                    onRefresh = onRefresh
                )
            }

            items(
                count = movies.itemCount,
                key = { index ->
                    movies.peek(index)?.id?.let { movieId -> "${listType.name}-$movieId-$index" }
                        ?: "${listType.name}-placeholder-$index"
                }
            ) { index ->
                movies[index]?.let { movie ->
                    TmdbWatchlistMovieCard(
                        movie = movie,
                        listType = listType,
                        onClick = {
                            onMovieClick(movie.id)
                        }
                    )
                }
            }

            when (movies.loadState.append) {
                is LoadState.Loading -> {
                    repeat(LIBRARY_APPEND_PLACEHOLDER_COUNT) { index ->
                        item(key = "${listType.name}-append-loading-$index") {
                            LibraryPosterPlaceholder()
                        }
                    }
                }

                is LoadState.Error -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LibraryAppendError(onRetry = onRefresh)
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun LibraryTabs(
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedTabIndex = LibraryTab.entries.indexOf(selectedTab)

    SecondaryTabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        divider = {},
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedTabIndex,
                    matchContentSize = true
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        LibraryTab.entries.forEach { tab ->
            val selected = selectedTab == tab

            Tab(
                selected = selected,
                onClick = { onTabSelected(tab) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        fontWeight = if (selected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.SemiBold
                        },
                        maxLines = 1
                    )
                }
            )
        }
    }
}

@Composable
private fun LibrarySyncStatus(
    listType: TmdbLibraryListType,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ReelsDimensions.favoritesSyncStatusCornerSize),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ReelsSpacing.medium,
                    vertical = ReelsSpacing.small
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(ReelsDimensions.favoritesSyncStatusDotSize)
                        .clip(CircleShape)
                        .background(ReelsTeal)
                )

                Text(
                    text = when (listType) {
                        TmdbLibraryListType.Watchlist -> "Synced via TMDB"
                        TmdbLibraryListType.Rated -> "Your TMDB ratings"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh TMDB library",
                modifier = Modifier
                    .size(ReelsDimensions.favoritesSyncStatusIconSize)
                    .clickable(role = Role.Button, onClick = onRefresh),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LibraryPosterPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesTextSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster)
                .clip(RoundedCornerShape(ReelsDimensions.favoritesPosterCornerSize))
                .background(libraryPlaceholderColor())
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(libraryPlaceholderColor())
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.36f)
                .height(11.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(libraryPlaceholderColor())
        )
    }
}

@Composable
private fun LibraryAppendError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReelsSpacing.medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Couldn't load more",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            modifier = Modifier.padding(start = ReelsSpacing.small),
            onClick = onRetry,
            contentPadding = PaddingValues(
                horizontal = ReelsSpacing.medium,
                vertical = ReelsSpacing.xSmall
            )
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
private fun TmdbWatchlistMovieCard(
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

@Composable
private fun FavoriteMovieCard(
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

@Composable
private fun SavedPersonCard(
    person: SavedPersonDataModel,
    isRemoving: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(ReelsDimensions.favoritesPosterCornerSize),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ReelsRatios.castPortrait)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                PersonProfileImage(
                    imageUrl = person.profilePath.toTmdbImageUrl(
                        imageType = TmdbImageType.Profile
                    ),
                    contentDescription = person.name,
                    modifier = Modifier.fillMaxSize()
                )

                OfflineBadge(
                    imageVector = Icons.Filled.WifiOff,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(ReelsSpacing.small)
                )

                LibraryRemoveButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(ReelsSpacing.small),
                    isRemoving = isRemoving,
                    contentDescription = "Remove ${person.name} from library",
                    onClick = onFavoriteClick
                )
            }

            Column(
                modifier = Modifier.padding(
                    horizontal = ReelsSpacing.medium,
                    vertical = ReelsSpacing.small
                ),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
            ) {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Work,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.favoritesMetadataIconSize),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = person.knownForDepartment ?: "Known for ${person.knownFor.size} movies",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonProfileImage(
    imageUrl: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        if (imageUrl == null) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.favoritesEmptyIconSize)
                )
            }
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(0.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun UserRatingBadge(
    rating: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = reelsRatingContainerColor().copy(alpha = 0.92f),
        contentColor = reelsImageRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.favoritesRatingHorizontalPadding,
                vertical = ReelsDimensions.favoritesRatingVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.favoritesRatingIconSize)
            )

            Text(
                text = rating.ifBlank { "–" },
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LibraryOverlayIconBadge(
    imageVector: ImageVector,
    contentDescription: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(ReelsDimensions.favoritesHeartButtonSize),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(ReelsDimensions.favoritesHeartIconSize)
            )
        }
    }
}

@Composable
private fun LibraryPosterCard(
    imageUrl: String?,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = {},
    topStartBadge: @Composable () -> Unit = {},
    topEndBadge: @Composable () -> Unit = {},
    bottomEndBadge: @Composable () -> Unit = {},
    showBottomScrim: Boolean = false,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ReelsRatios.poster),
        shape = RoundedCornerShape(ReelsDimensions.favoritesPosterCornerSize),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageUrl == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    placeholder()
                }
            } else {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(ReelsDimensions.favoritesPosterCornerSize)),
                    contentScale = ContentScale.Crop
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(ReelsSpacing.small)
            ) {
                topStartBadge()
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(ReelsDimensions.favoritesRatingEdgePadding)
            ) {
                topEndBadge()
            }

            if (showBottomScrim) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.76f)
                                )
                            )
                        )
                        .padding(ReelsDimensions.favoritesRatingEdgePadding),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    bottomEndBadge()
                }
            }
        }
    }
}

@Composable
private fun TmdbBadge(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(ReelsDimensions.favoritesOfflineBadgeSize),
        shape = MaterialTheme.shapes.medium,
        color = ReelsTeal.copy(alpha = 0.92f),
        contentColor = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = "Synced with TMDB",
                modifier = Modifier.size(ReelsDimensions.favoritesOfflineBadgeIconSize)
            )
        }
    }
}

@Composable
private fun OfflineBadge(
    imageVector: ImageVector = Icons.Filled.DownloadDone,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(ReelsDimensions.favoritesOfflineBadgeSize),
        shape = MaterialTheme.shapes.medium,
        color = ReelsTeal.copy(alpha = 0.88f),
        contentColor = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = imageVector,
                contentDescription = "Available offline",
                modifier = Modifier.size(ReelsDimensions.favoritesOfflineBadgeIconSize)
            )
        }
    }
}

@Composable
private fun RatingBadge(
    rating: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = reelsPosterRatingContainerColor(),
        contentColor = reelsRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.favoritesRatingHorizontalPadding,
                vertical = ReelsDimensions.favoritesRatingVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.favoritesRatingIconSize)
            )

            Text(
                text = rating,
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
private fun LibraryRemoveButton(
    isRemoving: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.favoritesHeartButtonSize)
            .clickable(
                enabled = !isRemoving,
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isRemoving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(ReelsDimensions.favoritesHeartIconSize),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.favoritesHeartIconSize)
                )
            }
        }
    }
}

@Composable
private fun LibraryErrorState(
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
            text = when (listType) {
                TmdbLibraryListType.Watchlist -> "Couldn't load your watchlist"
                TmdbLibraryListType.Rated -> "Couldn't load your ratings"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Check your connection and try again.",
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
private fun DisconnectedTmdbLibraryState(
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
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
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
                TmdbFeatureRow(
                    title = "Ratings sync",
                    description = "Your TMDB ratings in one place",
                    imageVector = Icons.Filled.Star
                )
                TmdbFeatureRow(
                    title = "Offline access",
                    description = "Cache synced content locally",
                    imageVector = Icons.Filled.DownloadDone
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onConnectSettingsClick
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
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
            Text(
                text = "TMDB",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                maxLines = 1
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
private fun EmptyLibraryState(
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

@Composable
private fun libraryPlaceholderColor(): Color {
    return MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
}

private const val LIBRARY_LOADING_PLACEHOLDER_COUNT = 6
private const val LIBRARY_APPEND_PLACEHOLDER_COUNT = 2
