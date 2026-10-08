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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.CachedContentBanner
import com.norbertotaveras.reels.presentation.ui.extensions.fullBleedHorizontal
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
internal fun FavoritesContent(
    state: FavoritesUiState,
    tmdbWatchlistMovies: LazyPagingItems<MovieDataModel>,
    tmdbRatedMovies: LazyPagingItems<MovieDataModel>,
    onEvent: (FavoritesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUsingCachedTmdbContent = when (state.selectedTab) {
        LibraryTab.Tmdb -> tmdbWatchlistMovies.loadState.refresh is LoadState.Error &&
            tmdbWatchlistMovies.itemCount > 0
        LibraryTab.Rated -> tmdbRatedMovies.loadState.refresh is LoadState.Error &&
            tmdbRatedMovies.itemCount > 0
        else -> false
    }
    var dismissedCachedNoticeTab by rememberSaveable {
        mutableStateOf<LibraryTab?>(null)
    }
    LaunchedEffect(isUsingCachedTmdbContent) {
        if (!isUsingCachedTmdbContent) dismissedCachedNoticeTab = null
    }

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
                isOffline = isUsingCachedTmdbContent,
                onTabSelected = { tab ->
                    onEvent(FavoritesEvent.TabSelected(tab = tab))
                },
                modifier = Modifier.padding(bottom = ReelsSpacing.xSmall)
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
                        listType = TmdbLibraryList.Watchlist,
                        onConnectSettingsClick = {
                            onEvent(FavoritesEvent.ConnectSettingsClicked)
                        },
                        onMovieClick = { movieId ->
                            onEvent(FavoritesEvent.MovieClicked(movieId = movieId))
                        },
                        onMovieActionClick = { movieId ->
                            onEvent(FavoritesEvent.WatchlistMovieRemoved(movieId = movieId))
                        },
                        showCachedNotice = dismissedCachedNoticeTab != LibraryTab.Tmdb,
                        onCachedNoticeDismiss = {
                            dismissedCachedNoticeTab = LibraryTab.Tmdb
                        },
                        onRefresh = tmdbWatchlistMovies::refresh
                    )
                }

                LibraryTab.Rated -> {
                    tmdbAccountMovieItems(
                        state = state,
                        movies = tmdbRatedMovies,
                        listType = TmdbLibraryList.Rated,
                        onConnectSettingsClick = {
                            onEvent(FavoritesEvent.ConnectSettingsClicked)
                        },
                        onMovieClick = { movieId ->
                            onEvent(FavoritesEvent.MovieClicked(movieId = movieId))
                        },
                        onMovieActionClick = { movieId ->
                            onEvent(FavoritesEvent.RatedMovieRemoved(movieId = movieId))
                        },
                        showCachedNotice = dismissedCachedNoticeTab != LibraryTab.Rated,
                        onCachedNoticeDismiss = {
                            dismissedCachedNoticeTab = LibraryTab.Rated
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
    isOffline: Boolean,
    onTabSelected: (LibraryTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesHeaderToBadgeSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Library",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp,
                    lineHeight = 36.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            )

            if (isOffline) LibraryOfflineStatus()
        }

        Text(
            text = state.librarySubtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
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

private fun androidx.compose.foundation.lazy.grid.LazyGridScope.tmdbAccountMovieItems(
    state: FavoritesUiState,
    movies: LazyPagingItems<MovieDataModel>,
    listType: TmdbLibraryList,
    onConnectSettingsClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onMovieActionClick: (Int) -> Unit,
    showCachedNotice: Boolean,
    onCachedNoticeDismiss: () -> Unit,
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

        movies.loadState.refresh is LoadState.Loading && movies.itemCount == 0 -> {
            repeat(LIBRARY_LOADING_PLACEHOLDER_COUNT) { index ->
                item(key = "${listType.name}-loading-$index") {
                    LibraryPosterPlaceholder()
                }
            }
        }

        movies.loadState.refresh is LoadState.Error && movies.itemCount == 0 -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LibraryErrorState(
                    onRetry = onRefresh
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
            val isUsingCachedContent = movies.loadState.refresh is LoadState.Error
            if (isUsingCachedContent && showCachedNotice) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CachedContentBanner(
                        message = when (listType) {
                            TmdbLibraryList.Watchlist -> "Cached watchlist"
                            TmdbLibraryList.Rated -> "Cached ratings"
                        },
                        supportingText = state.tmdbSyncedAtMillis[listType].toTmdbSyncLabel(),
                        onDismiss = onCachedNoticeDismiss
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                LibrarySyncStatus(
                    listType = listType,
                    syncedAtMillis = state.tmdbSyncedAtMillis[listType],
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
                    TmdbAccountMovieCard(
                        movie = movie,
                        listType = listType,
                        activityAtMillis = state.tmdbActivityTimes[listType]?.get(movie.id),
                        isUpdating = state.updatingTmdbMovieIds.contains(movie.id),
                        onClick = {
                            onMovieClick(movie.id)
                        },
                        onActionClick = {
                            onMovieActionClick(movie.id)
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

    Box(
        modifier = modifier.fullBleedHorizontal(ReelsSpacing.screenHorizontal),
        contentAlignment = Alignment.BottomCenter
    ) {
        HorizontalDivider(color = reelsBorderColor())

        SecondaryTabRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ReelsDimensions.favoritesTabHorizontalPadding),
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(
                            selectedTabIndex = selectedTabIndex,
                            matchContentSize = false
                        )
                        .padding(
                            horizontal = ReelsDimensions.favoritesTabIndicatorHorizontalPadding
                        )
                        .height(ReelsDimensions.favoritesTabIndicatorHeight),
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
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LibrarySyncStatus(
    listType: TmdbLibraryList,
    syncedAtMillis: Long?,
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
                    horizontal = ReelsSpacing.large,
                    vertical = ReelsSpacing.medium
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
                        TmdbLibraryList.Rated -> {
                            "Your TMDB ratings · ${syncedAtMillis.toTmdbSyncLabel()}"
                        }
                        TmdbLibraryList.Watchlist -> {
                            "Synced via TMDB · ${syncedAtMillis.toTmdbSyncLabel()}"
                        }
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clickable(role = Role.Button, onClick = onRefresh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh TMDB library",
                    modifier = Modifier.size(ReelsDimensions.favoritesSyncStatusIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LibraryOfflineStatus(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = ReelsTeal.copy(alpha = 0.12f),
        contentColor = ReelsTeal
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsSpacing.small,
                vertical = ReelsSpacing.xSmall
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.favoritesOfflineStatusIconSize)
            )
            Text(
                text = "Offline",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
