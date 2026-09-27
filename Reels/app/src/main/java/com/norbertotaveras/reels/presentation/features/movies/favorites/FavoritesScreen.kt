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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatingContent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSurfaceHigh
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbBlue
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbOnBlue
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor

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
                    state.people.forEach { person ->
                        item(
                            key = person.id,
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
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
                        }
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
                        }
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
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = state.librarySubtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LibraryTabs(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ReelsSpacing.small),
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
) {
    when {
        !state.isTmdbAccountConnected -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DisconnectedTmdbLibraryState(
                    listType = listType,
                    onConnectSettingsClick = onConnectSettingsClick,
                    modifier = Modifier.padding(top = ReelsDimensions.favoritesEmptyTopPadding)
                )
            }
        }

        movies.loadState.refresh is LoadState.Loading -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LibraryLoadingState(
                    listType = listType,
                    modifier = Modifier.padding(top = ReelsDimensions.favoritesEmptyTopPadding)
                )
            }
        }

        movies.loadState.refresh is LoadState.Error -> {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LibraryErrorState(
                    listType = listType,
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
                    matchContentSize = false
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
                        style = MaterialTheme.typography.labelLarge,
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
private fun TmdbWatchlistMovieCard(
    movie: MovieDataModel,
    listType: TmdbLibraryListType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(ReelsDimensions.favoritesPosterCardWidth),
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
                        contentColor = ReelsRatingContent
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
                    fontSize = 15.sp,
                    lineHeight = 20.sp
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
        modifier = modifier.width(ReelsDimensions.favoritesPosterCardWidth),
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
                    fontSize = 15.sp,
                    lineHeight = 20.sp
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ReelsDimensions.favoritesPersonRowHorizontalPadding,
                    vertical = ReelsDimensions.favoritesPersonRowVerticalPadding
                ),
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesPersonRowContentSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                PersonProfileImage(
                    imageUrl = person.profilePath.toTmdbImageUrl(
                        imageType = TmdbImageType.Profile
                    ),
                    contentDescription = person.name
                )

                OfflineBadge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(ReelsSpacing.xSmall)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
            ) {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleSmall,
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
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier.padding(start = ReelsSpacing.xSmall),
                contentAlignment = Alignment.Center
            ) {
                LibraryRemoveButton(
                    isRemoving = isRemoving,
                    contentDescription = "Remove ${person.name} from library",
                    onClick = onFavoriteClick
                )
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
        modifier = modifier.size(ReelsDimensions.favoritesPersonProfileSize),
        shape = RoundedCornerShape(ReelsDimensions.favoritesPersonProfileCornerSize),
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
                    .clip(RoundedCornerShape(ReelsDimensions.favoritesPersonProfileCornerSize)),
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
        contentColor = ReelsRatingContent
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.favoritesRatingHorizontalPadding,
                vertical = ReelsDimensions.favoritesRatingVerticalPadding
            ),
            text = if (rating.isBlank()) "Your" else "Your $rating",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
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
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.88f),
        contentColor = MaterialTheme.colorScheme.background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Sync,
                contentDescription = "Synced with TMDB",
                modifier = Modifier.size(ReelsDimensions.favoritesOfflineBadgeIconSize)
            )
        }
    }
}

@Composable
private fun OfflineBadge(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(ReelsDimensions.favoritesOfflineBadgeSize),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.88f),
        contentColor = MaterialTheme.colorScheme.background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.DownloadDone,
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
        color = ReelsSurfaceHigh.copy(alpha = 0.82f),
        contentColor = ReelsRatingContent
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
private fun LibraryLoadingState(
    listType: TmdbLibraryListType,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
        )
    ) {
        Column(
            modifier = Modifier.padding(ReelsDimensions.favoritesEmptyCardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesEmptyContentSpacing)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)

            Text(
                text = when (listType) {
                    TmdbLibraryListType.Watchlist -> "Loading TMDB watchlist"
                    TmdbLibraryListType.Rated -> "Loading TMDB ratings"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LibraryErrorState(
    listType: TmdbLibraryListType,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
        )
    ) {
        Column(
            modifier = Modifier.padding(ReelsDimensions.favoritesEmptyCardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesEmptyContentSpacing)
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.favoritesEmptyIconContainerSize),
                shape = MaterialTheme.shapes.large,
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
                    TmdbLibraryListType.Watchlist -> "Unable to load TMDB watchlist"
                    TmdbLibraryListType.Rated -> "Unable to load TMDB ratings"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Check your connection or reconnect TMDB from Settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DisconnectedTmdbLibraryState(
    listType: TmdbLibraryListType,
    onConnectSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
        )
    ) {
        Column(
            modifier = Modifier.padding(ReelsDimensions.favoritesEmptyCardPadding),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesEmptyContentSpacing)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LibraryTmdbLogoMark()

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
                ) {
                    Text(
                        text = "Connect TMDB to sync your library",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = when (listType) {
                            TmdbLibraryListType.Watchlist -> {
                                "Bring your TMDB watchlist into Reels."
                            }
                            TmdbLibraryListType.Rated -> {
                                "Bring your TMDB ratings into Reels."
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
            ) {
                TmdbFeatureRow(text = "Sync your TMDB watchlist")
                TmdbFeatureRow(text = "View your movie ratings")
                TmdbFeatureRow(text = "Local favorites stay available offline")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onConnectSettingsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReelsTmdbBlue,
                    contentColor = ReelsTmdbOnBlue
                )
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
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.favoritesFeatureIconSize)
                )
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EmptyLibraryState(
    selectedTab: LibraryTab,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
        )
    ) {
        Column(
            modifier = Modifier.padding(ReelsDimensions.favoritesEmptyCardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesEmptyContentSpacing)
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.favoritesEmptyIconContainerSize),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (selectedTab) {
                            LibraryTab.Movies -> Icons.Outlined.FavoriteBorder
                            LibraryTab.People -> Icons.Filled.Person
                            LibraryTab.Tmdb -> Icons.Filled.Sync
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
                    LibraryTab.Tmdb -> "No TMDB watchlist yet"
                    LibraryTab.Rated -> "No TMDB ratings yet"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = when (selectedTab) {
                    LibraryTab.Movies -> "Save movies from details to keep their posters, ratings, and key info available offline."
                    LibraryTab.People -> "Save people from cast details to keep their profiles and known-for movies available offline."
                    LibraryTab.Tmdb -> "Connect TMDB in Settings, then add movies to your TMDB watchlist from Movie Details."
                    LibraryTab.Rated -> "Rate movies from Movie Details to sync them with your TMDB account."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
