/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.components.movie

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.extensions.toRatingText
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContentColor

@Composable
internal fun HeroActionRow(
    rating: String?,
    isFavorite: Boolean,
    isFavoriteLoading: Boolean,
    isWatchlisted: Boolean,
    tmdbRating: Double?,
    isAccountActionLoading: Boolean,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onRatingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        if (!rating.isNullOrBlank()) {
            HeroRatingButton(rating = rating)
        }

        HeroFavoriteButton(
            isFavorite = isFavorite,
            isLoading = isFavoriteLoading,
            onClick = onFavoriteClick
        )

        HeroToggleActionButton(
            selected = isWatchlisted,
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder,
            selectedContentDescription = "Remove from TMDB watchlist",
            unselectedContentDescription = "Add to TMDB watchlist",
            selectedColor = ReelsTeal,
            isLoading = isAccountActionLoading,
            onClick = onWatchlistClick
        )

        HeroRateButton(
            rating = tmdbRating,
            isLoading = isAccountActionLoading,
            onClick = onRatingClick
        )
    }
}

@Composable
private fun HeroRatingButton(
    rating: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(ReelsDimensions.detailsActionButtonHeight),
        shape = RoundedCornerShape(8.dp),
        color = reelsRatingContainerColor(),
        contentColor = reelsRatingContentColor()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = ReelsSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.detailsHeroRatingIconSize)
            )

            Text(
                text = rating,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun HeroFavoriteButton(
    isFavorite: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.detailsActionButtonHeight)
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClickLabel = if (isFavorite) {
                    "Remove from favorites"
                } else {
                    "Add to favorites"
                },
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (isFavorite) {
            reelsRatingContainerColor()
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (isFavorite) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isFavorite) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            }
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.detailsHeroActionIconSize)
                )
            }
        }
    }
}

@Composable
private fun HeroToggleActionButton(
    selected: Boolean,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedContentDescription: String,
    unselectedContentDescription: String,
    selectedColor: Color,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.detailsActionButtonHeight)
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClickLabel = if (selected) {
                    selectedContentDescription
                } else {
                    unselectedContentDescription
                },
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) {
            selectedColor.copy(alpha = 0.20f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (selected) {
            selectedColor
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                selectedColor.copy(alpha = 0.60f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            }
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.detailsHeroActionIconSize)
                )
            }
        }
    }
}

@Composable
private fun HeroRateButton(
    rating: Double?,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.detailsActionButtonHeight)
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClickLabel = if (rating == null) "Rate on TMDB" else "Update TMDB rating",
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (rating == null) {
            MaterialTheme.colorScheme.surface
        } else {
            reelsRatingContainerColor()
        },
        contentColor = if (rating == null) {
            MaterialTheme.colorScheme.onSurface
        } else {
            reelsRatingContentColor()
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (rating == null) {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                if (rating == null) {
                    Icon(
                        imageVector = Icons.Outlined.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.detailsHeroActionIconSize)
                    )
                } else {
                    Text(
                        text = rating.toRatingText(),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}
