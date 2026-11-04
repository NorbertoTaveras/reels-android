/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.presentation.ui.components.common.MessageState
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbBlue
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbOnBlue

@Composable
internal fun LibraryErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MessageState(
        modifier = modifier,
        icon = Icons.Outlined.ErrorOutline,
        title = "Couldn't load movies",
        message = "Check your connection and try again.",
        actionLabel = "Retry",
        actionIcon = Icons.Outlined.Refresh,
        onAction = onRetry
    )
}

@Composable
internal fun DisconnectedTmdbLibraryState(
    onConnectSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsDimensions.favoritesDisconnectedHorizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LibraryTmdbLogoMark()
        Spacer(modifier = Modifier.height(ReelsDimensions.favoritesDisconnectedLogoSpacing))

        Text(
            text = "Connect to TMDB",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = ReelsDimensions.favoritesDisconnectedTitleSize,
                lineHeight = ReelsDimensions.favoritesDisconnectedTitleLineHeight,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(ReelsDimensions.favoritesDisconnectedTitleSpacing))

        Text(
            text = "Sign in to your TMDB account to sync your watchlist and ratings across devices.",
            modifier = Modifier.widthIn(
                max = ReelsDimensions.favoritesDisconnectedBodyMaxWidth
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = ReelsDimensions.favoritesDisconnectedBodySize,
                lineHeight = ReelsDimensions.favoritesDisconnectedBodyLineHeight
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(ReelsDimensions.favoritesDisconnectedSectionSpacing))

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
        Spacer(modifier = Modifier.height(ReelsDimensions.favoritesDisconnectedSectionSpacing))

        Button(
            modifier = Modifier
                .width(ReelsDimensions.favoritesConnectButtonWidth)
                .height(ReelsDimensions.favoritesConnectButtonHeight),
            onClick = onConnectSettingsClick,
            shape = RoundedCornerShape(ReelsDimensions.favoritesConnectButtonCornerSize),
            contentPadding = PaddingValues(
                horizontal = ReelsDimensions.favoritesConnectButtonHorizontalPadding
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Link,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.favoritesConnectButtonIconSize)
            )

            Text(
                modifier = Modifier.padding(start = ReelsSpacing.small),
                text = "Connect in Settings",
                fontWeight = FontWeight.SemiBold
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
        shape = RoundedCornerShape(ReelsDimensions.favoritesTmdbLogoCornerSize),
        color = ReelsTmdbBlue,
        contentColor = ReelsTmdbOnBlue
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.tmdb_logo),
                contentDescription = "TMDB logo",
                modifier = Modifier.width(ReelsDimensions.favoritesTmdbLogoMarkWidth),
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
                horizontal = ReelsDimensions.favoritesFeatureRowHorizontalPadding,
                vertical = ReelsDimensions.favoritesFeatureRowVerticalPadding
            ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.favoritesFeatureRowSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.favoritesFeatureIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.favoritesFeatureIconCornerSize),
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

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = ReelsDimensions.favoritesFeatureTitleSize,
                    lineHeight = ReelsDimensions.favoritesFeatureTitleLineHeight,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = ReelsDimensions.favoritesFeatureDescriptionSize,
                    lineHeight = ReelsDimensions.favoritesFeatureDescriptionLineHeight,
                    fontWeight = FontWeight.Normal
                ),
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
            .padding(horizontal = ReelsDimensions.favoritesLibraryEmptyHorizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.favoritesLibraryEmptyIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.favoritesLibraryEmptyIconCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (selectedTab) {
                        LibraryTab.Movies -> Icons.Outlined.Movie
                        LibraryTab.People -> Icons.Outlined.People
                        LibraryTab.Tmdb -> Icons.Outlined.Bookmark
                        LibraryTab.Rated -> Icons.Filled.Star
                    },
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.favoritesLibraryEmptyIconSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(ReelsDimensions.favoritesLibraryEmptyIconToTitleSpacing))

        Text(
            text = when (selectedTab) {
                LibraryTab.Movies -> "No saved movies yet"
                LibraryTab.People -> "No saved people yet"
                LibraryTab.Tmdb -> "Watchlist is empty"
                LibraryTab.Rated -> "No rated movies yet"
            },
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = ReelsDimensions.favoritesLibraryEmptyTitleSize,
                lineHeight = ReelsDimensions.favoritesLibraryEmptyTitleLineHeight,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ReelsDimensions.favoritesLibraryEmptyTitleToBodySpacing))

        Text(
            text = when (selectedTab) {
                LibraryTab.Movies -> "Tap the heart on any movie to save it here for offline access."
                LibraryTab.People -> "Save cast members from any detail page."
                LibraryTab.Tmdb -> "Add movies from any Movie Details page."
                LibraryTab.Rated -> "Rate movies from any Movie Details page."
            },
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = ReelsDimensions.favoritesLibraryEmptyBodySize,
                lineHeight = ReelsDimensions.favoritesLibraryEmptyBodyLineHeight,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
