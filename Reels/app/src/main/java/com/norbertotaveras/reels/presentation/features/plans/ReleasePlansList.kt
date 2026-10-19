/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import java.time.LocalDate

@Composable
internal fun ReleasePlansList(
    releases: List<TrackedRelease>,
    onMovieClick: (Int) -> Unit,
    onRemove: (Int) -> Unit,
) {
    if (releases.isEmpty()) {
        PlansEmptyState(
            icon = { Icon(Icons.Filled.EventAvailable, null) },
            title = "No tracked releases",
            message = "Track a movie from Movie Details to follow its release date.",
        )
        return
    }
    val today = LocalDate.now()
    val (released, upcoming) = releases.partition { release ->
        release.releaseDate?.let { runCatching { LocalDate.parse(it).isBefore(today) }.getOrDefault(false) } == true
    }
    LazyColumn(contentPadding = PaddingValues(horizontal = ReelsDimensions.plansListHorizontalPadding)) {
        items(upcoming, key = { it.movieId }) { release ->
            ReleasePlanRow(release, onMovieClick, onRemove)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
        }
        if (released.isNotEmpty()) {
            item(key = "released-heading") { ReleasedHeading() }
            items(released, key = { "released-${it.movieId}" }) { release ->
                ReleasePlanRow(release, onMovieClick, onRemove)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
            }
        }
    }
}

@Composable
private fun ReleasePlanRow(
    release: TrackedRelease,
    onMovieClick: (Int) -> Unit,
    onRemove: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(release.movieId) }
            .padding(vertical = ReelsDimensions.plansReleaseRowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = release.posterPath.toTmdbImageUrl(),
            contentDescription = release.title,
            modifier = Modifier
                .width(ReelsDimensions.plansPosterWidth)
                .height(ReelsDimensions.plansPosterHeight)
                .clip(RoundedCornerShape(ReelsDimensions.plansPosterCornerSize)),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(ReelsDimensions.plansRowSpacing))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.plansMetadataSpacing),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    release.title,
                    modifier = Modifier.weight(1f, fill = false),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                release.releaseStatusLabel()?.let { status -> ReleaseStatusBadge(status) }
            }
            PlanMetadataLine(Icons.Filled.CalendarMonth, release.releaseDateLabel())
            release.previousDateLabel()?.let { previous ->
                Text(
                    "Was $previous",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough,
                )
            }
            PlanMetadataLine(Icons.Filled.Alarm, release.reminderLabel())
        }
        ReleaseOverflowMenu(onRemove = { onRemove(release.movieId) })
    }
}

@Composable
private fun ReleaseStatusBadge(status: String) {
    Surface(
        shape = RoundedCornerShape(ReelsDimensions.plansStatusCornerSize),
        color = when (status) {
            "Out today" -> MaterialTheme.colorScheme.tertiaryContainer
            "Date moved" -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
    ) {
        Text(
            status,
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.plansStatusHorizontalPadding,
                vertical = ReelsDimensions.plansStatusVerticalPadding,
            ),
            style = MaterialTheme.typography.labelSmall,
            color = when (status) {
                "Out today" -> MaterialTheme.colorScheme.onTertiaryContainer
                "Date moved" -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}

@Composable
private fun PlanMetadataLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansMetadataSpacing),
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(ReelsDimensions.plansMetadataIconSize),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ReleaseOverflowMenu(onRemove: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Release options")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Stop tracking") },
                leadingIcon = { Icon(Icons.Filled.DeleteOutline, null) },
                onClick = { expanded = false; onRemove() },
            )
        }
    }
}

@Composable
private fun ReleasedHeading() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text("RELEASED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}
