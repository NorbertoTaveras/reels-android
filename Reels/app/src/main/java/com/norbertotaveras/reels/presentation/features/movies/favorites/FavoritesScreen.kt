package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.extensions.toReleaseYear
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsBorder
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSurfaceHigh
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onEvent: (FavoritesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.movies.isEmpty()) {
        EmptyFavoritesState(modifier = modifier.fillMaxSize())
        return
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
        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesHeaderToBadgeSpacing)
            ) {
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DownloadDone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(ReelsDimensions.favoritesOfflineBadgeIconSize)
                    )

                    Text(
                        text = "Available offline",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        items(
            items = state.movies,
            key = { movie -> movie.id }
        ) { movie ->
            FavoriteMovieCard(
                movie = movie,
                isRemoving = state.removingMovieIds.contains(movie.id),
                onClick = { onEvent(FavoritesEvent.MovieClicked(movieId = movie.id)) },
                onFavoriteClick = { onEvent(FavoritesEvent.FavoriteClicked(movieId = movie.id)) }
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ReelsRatios.poster),
            shape = RoundedCornerShape(ReelsDimensions.favoritesPosterCornerSize),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            onClick = onClick
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = movie.posterPath.toTmdbImageUrl(),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(ReelsDimensions.favoritesPosterCornerSize)),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(ReelsSpacing.small)
                        .size(ReelsDimensions.favoritesOfflineBadgeSize),
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

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(ReelsDimensions.favoritesRatingEdgePadding),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = ReelsSurfaceHigh.copy(alpha = 0.82f),
                    contentColor = MaterialTheme.colorScheme.primary
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
                            text = String.format("%.1f", movie.voteAverage),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesCardActionSpacing),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
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

            Surface(
                modifier = Modifier
                    .size(ReelsDimensions.favoritesHeartButtonSize)
                    .clickable(
                        enabled = !isRemoving,
                        role = Role.Button,
                        onClick = onFavoriteClick
                    ),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                border = BorderStroke(
                    width = 1.dp,
                    color = ReelsBorder
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
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Remove favorite movie",
                            modifier = Modifier.size(ReelsDimensions.favoritesHeartIconSize)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(ReelsSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesEmptyTopPadding)
    ) {
        Text(
            modifier = Modifier.padding(top = ReelsDimensions.favoritesScreenTopPadding),
            text = "Favorites",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = ReelsBorder
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
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(ReelsDimensions.favoritesEmptyIconSize)
                        )
                    }
                }

                Text(
                    text = "No saved movies yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Save movies from details to keep their posters, ratings, and key info available offline.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
