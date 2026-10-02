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
import androidx.compose.material3.Button
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal

@Composable
internal fun FavoritesContent(
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

private fun androidx.compose.foundation.lazy.grid.LazyGridScope.tmdbAccountMovieItems(
    state: FavoritesUiState,
    movies: LazyPagingItems<MovieDataModel>,
    listType: TmdbLibraryList,
    onConnectSettingsClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onMovieActionClick: (Int) -> Unit,
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
                    TmdbAccountMovieCard(
                        movie = movie,
                        listType = listType,
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
    listType: TmdbLibraryList,
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
                        TmdbLibraryList.Watchlist -> "Synced via TMDB"
                        TmdbLibraryList.Rated -> "Your TMDB ratings"
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
