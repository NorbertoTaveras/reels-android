/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings.states

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsBackground
import com.norbertotaveras.reels.presentation.ui.theme.ReelsBorder
import com.norbertotaveras.reels.presentation.ui.theme.ReelsError
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightBackground
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightBorder
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightSurface
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightSurfaceHigh
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightText
import com.norbertotaveras.reels.presentation.ui.theme.ReelsLightTextSecondary
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSurface
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSurfaceHigh
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTextPrimary
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTextSecondary
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbBlue

@Composable
fun UiStatesReferenceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = ReelsSpacing.screenHorizontal,
            top = ReelsSpacing.large,
            end = ReelsSpacing.screenHorizontal,
            bottom = ReelsSpacing.xxLarge
        ),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xLarge)
    ) {
        item {
            ReferenceHeader(onBackClick = onBackClick)
        }

        item {
            ThemeLegend()
        }

        item {
            ReferenceSectionTitle(title = "LOADING SKELETONS")
        }

        item {
            ReferenceSpecimen(title = "POSTER GRID (3-COL)") { palette ->
                PosterGridSkeleton(palette = palette)
            }
        }

        item {
            ReferenceSpecimen(title = "SEARCH RESULT ROWS") { palette ->
                SearchRowsSkeleton(palette = palette)
            }
        }

        item {
            ReferenceSpecimen(title = "MOVIE LIST ROWS") { palette ->
                MovieRowsSkeleton(palette = palette)
            }
        }

        item {
            ReferenceSpecimen(title = "CAST AVATAR ROW") { palette ->
                CastRowSkeleton(palette = palette)
            }
        }

        item {
            ReferenceSectionTitle(title = "EMPTY STATES")
        }

        item {
            EmptyStateSpecimen(
                title = "NO SAVED MOVIES (LIBRARY · MOVIES)",
                icon = Icons.Filled.Movie,
                stateTitle = "No saved movies yet",
                message = "Tap the heart on any movie to save it here for offline access."
            )
        }

        item {
            EmptyStateSpecimen(
                title = "NO SAVED PEOPLE (LIBRARY · PEOPLE)",
                icon = Icons.Filled.Person,
                stateTitle = "No saved people yet",
                message = "Save cast members from any detail page."
            )
        }

        item {
            EmptyStateSpecimen(
                title = "WATCHLIST EMPTY (LIBRARY · WATCHLIST)",
                icon = Icons.Outlined.BookmarkBorder,
                stateTitle = "Watchlist is empty",
                message = "Add movies from any Movie Details page."
            )
        }

        item {
            EmptyStateSpecimen(
                title = "TMDB DISCONNECTED (WATCHLIST / RATED)",
                icon = Icons.Filled.CloudOff,
                stateTitle = "Connect to TMDB",
                message = "Sign in to sync your watchlist and ratings.",
                actionLabel = "Connect in Settings"
            )
        }

        item {
            EmptyStateSpecimen(
                title = "NO RESULTS (DISCOVER / SEARCH)",
                icon = Icons.Filled.SearchOff,
                stateTitle = "No results found",
                message = "Try a different title or keyword.",
                actionLabel = "Clear filters"
            )
        }

        item {
            ReferenceSectionTitle(title = "ERROR / RETRY")
        }

        item {
            ErrorStateSpecimen(
                title = "FULL-PAGE ERROR (DISCOVER · LIBRARY · FILMOGRAPHY)",
                stateTitle = "Couldn't load movies",
                message = "Check your connection and try again."
            )
        }

        item {
            CompactStateSpecimen(
                title = "LOAD-MORE ERROR FOOTER (CATEGORY SEE ALL · DISCOVER)",
                icon = Icons.Filled.WarningAmber,
                message = "Couldn't load more movies",
                actionLabel = "Retry"
            )
        }

        item {
            CompactStateSpecimen(
                title = "FILTER SHEET INLINE ERROR (GENRE FETCH FAIL)",
                icon = Icons.Filled.WarningAmber,
                message = "Couldn't load genres\nCheck your connection",
                actionLabel = "Retry"
            )
        }

        item {
            CompactStateSpecimen(
                title = "INLINE NETWORK NOTICE (LIST VIEW)",
                icon = Icons.Filled.WifiOff,
                message = "Network unavailable · Retry to refresh"
            )
        }

        item {
            ReferenceSectionTitle(title = "CACHE & OFFLINE BANNERS")
        }

        item {
            BannerSpecimen(
                title = "OFFLINE BANNER (LIBRARY · DISCOVER · FILMOGRAPHY)",
                icon = Icons.Filled.WifiOff,
                headline = "Offline",
                supporting = "Saved library available"
            )
        }

        item {
            BannerSpecimen(
                title = "CACHED RESULTS BANNER (SEARCH)",
                icon = Icons.Filled.DownloadDone,
                headline = "Showing cached results",
                supporting = "Searched 2 hours ago"
            )
        }

        item {
            BannerSpecimen(
                title = "SYNC STATUS BAR (LIBRARY · WATCHLIST / RATED)",
                icon = Icons.Filled.Sync,
                headline = "Synced via TMDB",
                supporting = "2 min ago"
            )
        }

        item {
            ReferenceSectionTitle(title = "OFFLINE ITEM NOTICES")
        }

        item {
            ReferenceSpecimen(title = "OFFLINE SAVED MOVIE — DOWNLOAD BADGE + HEART REMOVE") { palette ->
                OfflineMoviePreview(palette = palette)
            }
        }

        item {
            ReferenceSpecimen(title = "OFFLINE SAVED PERSON — WIFIOFF BADGE + HEART REMOVE") { palette ->
                OfflinePersonPreview(palette = palette)
            }
        }

        item {
            ReferenceSectionTitle(title = "SNACKBARS")
        }

        item {
            SnackbarSpecimen(
                title = "SYNC ERROR — ALERT · RETRY · ×",
                icon = Icons.Filled.WarningAmber,
                message = "Couldn't sync watchlist",
                actionLabel = "Retry",
                destructive = true
            )
        }

        item {
            SnackbarSpecimen(
                title = "SYNC IN PROGRESS — SPINNER",
                icon = Icons.Filled.Sync,
                message = "Syncing with TMDB…"
            )
        }

        item {
            SnackbarSpecimen(
                title = "SUCCESS · SAVED OFFLINE",
                icon = Icons.Filled.CheckCircle,
                message = "Saved offline"
            )
        }

        item {
            SnackbarSpecimen(
                title = "SUCCESS · REMOVED FROM LIBRARY",
                icon = Icons.Filled.FavoriteBorder,
                message = "Removed from library"
            )
        }

        item {
            SnackbarSpecimen(
                title = "SUCCESS · ADDED TO WATCHLIST",
                icon = Icons.Outlined.BookmarkBorder,
                message = "Added to Watchlist"
            )
        }

        item {
            SnackbarSpecimen(
                title = "SUCCESS · RATED — AMBER STAR ICON",
                icon = Icons.Filled.Star,
                message = "Rated 8/10"
            )
        }

        item {
            SnackbarSpecimen(
                title = "SUCCESS · IMAGE CACHE CLEARED",
                icon = Icons.Filled.DeleteSweep,
                message = "Image cache cleared · 42 MB freed"
            )
        }

        item {
            ReferenceSectionTitle(title = "TOKEN REFERENCE")
        }

        item {
            TokenReference()
        }
    }
}

@Composable
private fun ReferenceHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(ReelsSpacing.large))

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "UI States",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Production state reference",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ThemeLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ThemeLegendItem(label = "Dark theme", color = ReelsBackground)
        Spacer(modifier = Modifier.width(ReelsSpacing.xLarge))
        ThemeLegendItem(label = "Light theme", color = ReelsLightBackground)
    }
}

@Composable
private fun ThemeLegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ReferenceSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = ReelsSpacing.medium),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun ReferenceSpecimen(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (ReferencePalette) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        SpecimenTitle(text = title)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
        ) {
            ReferenceCard(
                label = "DARK",
                palette = DarkReferencePalette,
                modifier = Modifier.weight(1f),
                content = content
            )
            ReferenceCard(
                label = "LIGHT",
                palette = LightReferencePalette,
                modifier = Modifier.weight(1f),
                content = content
            )
        }
    }
}

@Composable
private fun ReferenceCard(
    label: String,
    palette: ReferencePalette,
    modifier: Modifier = Modifier,
    content: @Composable (ReferencePalette) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.background)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .padding(ReelsSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = palette.secondaryText,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(palette.border)
            )
        }
        content(palette)
    }
}

@Composable
private fun PosterGridSkeleton(palette: ReferencePalette) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(2) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SkeletonBlock(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.68f),
                            palette = palette
                        )
                        SkeletonBlock(Modifier.fillMaxWidth(0.82f).height(5.dp), palette)
                        SkeletonBlock(Modifier.fillMaxWidth(0.55f).height(4.dp), palette)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchRowsSkeleton(palette: ReferencePalette) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surface)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkeletonBlock(Modifier.width(38.dp).height(56.dp), palette)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.9f).height(6.dp), palette)
                    SkeletonBlock(Modifier.fillMaxWidth(0.55f).height(5.dp), palette)
                    SkeletonBlock(Modifier.fillMaxWidth(0.4f).height(12.dp), palette)
                }
            }
        }
    }
}

@Composable
private fun MovieRowsSkeleton(palette: ReferencePalette) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBlock(Modifier.width(42.dp).height(62.dp), palette)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.82f).height(6.dp), palette)
                    SkeletonBlock(Modifier.fillMaxWidth(0.5f).height(5.dp), palette)
                }
            }
        }
    }
}

@Composable
private fun CastRowSkeleton(palette: ReferencePalette) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(3) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                SkeletonBlock(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape),
                    palette = palette
                )
                SkeletonBlock(Modifier.fillMaxWidth(0.8f).height(5.dp), palette)
                SkeletonBlock(Modifier.fillMaxWidth(0.55f).height(4.dp), palette)
            }
        }
    }
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier,
    palette: ReferencePalette,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(palette.surfaceHigh)
    )
}

@Composable
private fun EmptyStateSpecimen(
    title: String,
    icon: ImageVector,
    stateTitle: String,
    message: String,
    actionLabel: String? = null,
) {
    ReferenceSpecimen(title = title) { palette ->
        EmptyPreview(
            palette = palette,
            icon = icon,
            title = stateTitle,
            message = message,
            actionLabel = actionLabel
        )
    }
}

@Composable
private fun EmptyPreview(
    palette: ReferencePalette,
    icon: ImageVector,
    title: String,
    message: String,
    actionLabel: String?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(18.dp), tint = palette.accent)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = palette.text,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            color = palette.secondaryText,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null) {
            MiniButton(label = actionLabel, palette = palette)
        }
    }
}

@Composable
private fun ErrorStateSpecimen(title: String, stateTitle: String, message: String) {
    ReferenceSpecimen(title = title) { palette ->
        EmptyPreview(
            palette = palette,
            icon = Icons.Filled.CloudOff,
            title = stateTitle,
            message = message,
            actionLabel = "Retry"
        )
    }
}

@Composable
private fun CompactStateSpecimen(
    title: String,
    icon: ImageVector,
    message: String,
    actionLabel: String? = null,
) {
    ReferenceSpecimen(title = title) { palette ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surface)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(icon, null, Modifier.size(18.dp), tint = palette.accent)
            Text(
                text = message,
                style = MaterialTheme.typography.labelSmall,
                color = palette.secondaryText,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null) MiniButton(actionLabel, palette)
        }
    }
}

@Composable
private fun MiniButton(label: String, palette: ReferencePalette) {
    Button(
        onClick = {},
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 10.dp,
            vertical = 2.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.accent,
            contentColor = palette.background
        )
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun BannerSpecimen(
    title: String,
    icon: ImageVector,
    headline: String,
    supporting: String,
) {
    ReferenceSpecimen(title = title) { palette ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surface)
                .padding(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, Modifier.size(17.dp), tint = palette.accent)
            Spacer(modifier = Modifier.width(7.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = headline,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.text,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.secondaryText
                )
            }
            Icon(Icons.Filled.Refresh, null, Modifier.size(15.dp), tint = palette.secondaryText)
        }
    }
}

@Composable
private fun OfflineMoviePreview(palette: ReferencePalette) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PreviewPoster(palette = palette, offlineIcon = Icons.Filled.DownloadDone)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text("Dune: Part Two", style = MaterialTheme.typography.labelMedium, color = palette.text, fontWeight = FontWeight.Bold)
            Text("2024", style = MaterialTheme.typography.labelSmall, color = palette.secondaryText)
            Icon(Icons.Filled.FavoriteBorder, null, Modifier.size(17.dp), tint = palette.accent)
        }
    }
}

@Composable
private fun OfflinePersonPreview(palette: ReferencePalette) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, null, Modifier.size(26.dp), tint = palette.secondaryText)
            Icon(
                Icons.Filled.WifiOff,
                null,
                Modifier.align(Alignment.BottomEnd).size(17.dp),
                tint = palette.accent
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("T. Chalamet", style = MaterialTheme.typography.labelMedium, color = palette.text, fontWeight = FontWeight.Bold)
            Text("Acting", style = MaterialTheme.typography.labelSmall, color = palette.secondaryText)
        }
        Icon(Icons.Filled.FavoriteBorder, null, Modifier.size(17.dp), tint = palette.accent)
    }
}

@Composable
private fun PreviewPoster(palette: ReferencePalette, offlineIcon: ImageVector) {
    Box(
        modifier = Modifier
            .width(54.dp)
            .aspectRatio(0.68f)
            .clip(RoundedCornerShape(9.dp))
            .background(palette.surfaceHigh),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Movie, null, Modifier.size(24.dp), tint = palette.secondaryText)
        Icon(
            offlineIcon,
            null,
            Modifier.align(Alignment.TopStart).padding(4.dp).size(15.dp),
            tint = palette.accent
        )
    }
}

@Composable
private fun SnackbarSpecimen(
    title: String,
    icon: ImageVector,
    message: String,
    actionLabel: String? = null,
    destructive: Boolean = false,
) {
    ReferenceSpecimen(title = title) { palette ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surfaceHigh)
                .padding(horizontal = 9.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                Modifier.size(16.dp),
                tint = if (destructive) ReelsError else palette.accent
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = palette.text,
                fontWeight = FontWeight.SemiBold
            )
            if (actionLabel != null) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.accent,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Icon(Icons.Filled.Close, null, Modifier.size(14.dp), tint = palette.secondaryText)
        }
    }
}

@Composable
private fun TokenReference(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(ReelsSpacing.large),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        TokenRow("accent (amber)", ReelsAccent, ReelsLightAccent, "#d4a574  ·  #c49040")
        TokenRow("teal highlight", ReelsTeal, ReelsTeal, "#4a9b9b  ·  #4a9b9b")
        TokenRow("TMDB blue", ReelsTmdbBlue, ReelsTmdbBlue, "#01b4e4  ·  #01b4e4")
        TokenRow("destructive", ReelsError, ReelsError, "#c0392b  ·  #c0392b")
    }
}

@Composable
private fun TokenRow(
    label: String,
    darkColor: Color,
    lightColor: Color,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(darkColor)
        )
        Box(
            modifier = Modifier
                .padding(start = 3.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(lightColor)
        )
        Spacer(modifier = Modifier.width(ReelsSpacing.medium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SpecimenTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold
    )
}

private data class ReferencePalette(
    val background: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val text: Color,
    val secondaryText: Color,
    val border: Color,
    val accent: Color,
)

private val DarkReferencePalette = ReferencePalette(
    background = ReelsBackground,
    surface = ReelsSurface,
    surfaceHigh = ReelsSurfaceHigh,
    text = ReelsTextPrimary,
    secondaryText = ReelsTextSecondary,
    border = ReelsBorder,
    accent = ReelsAccent
)

private val LightReferencePalette = ReferencePalette(
    background = ReelsLightBackground,
    surface = ReelsLightSurface,
    surfaceHigh = ReelsLightSurfaceHigh,
    text = ReelsLightText,
    secondaryText = ReelsLightTextSecondary,
    border = ReelsLightBorder,
    accent = ReelsLightAccent
)

@Preview(showBackground = true)
@Composable
private fun UiStatesReferenceScreenPreview() {
    ReelsTheme(darkTheme = true) {
        UiStatesReferenceScreen(onBackClick = {})
    }
}
