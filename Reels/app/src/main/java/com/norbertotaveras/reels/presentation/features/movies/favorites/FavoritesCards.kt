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
internal fun LibraryPosterPlaceholder(
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
                .background(reelsPlaceholderColor(alpha = 0.72f))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(reelsPlaceholderColor(alpha = 0.72f))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.36f)
                .height(11.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(reelsPlaceholderColor(alpha = 0.72f))
        )
    }
}

@Composable
internal fun LibraryAppendError(
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

@Composable
internal fun SavedPersonCard(
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
