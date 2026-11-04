/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.release

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.NotificationsOff
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease
import com.norbertotaveras.reels.presentation.features.plans.overview.PlansEmptyState
import com.norbertotaveras.reels.presentation.features.plans.shared.hasActiveReminder
import com.norbertotaveras.reels.presentation.features.plans.shared.previousDateLabel
import com.norbertotaveras.reels.presentation.features.plans.shared.releaseDateLabel
import com.norbertotaveras.reels.presentation.features.plans.shared.releaseStatusLabel
import com.norbertotaveras.reels.presentation.features.plans.shared.reminderLabel
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor
import java.time.LocalDate

@Composable
internal fun ReleasePlansList(
    releases: List<TrackedRelease>,
    onMovieClick: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onEdit: (TrackedRelease) -> Unit,
    onBrowse: () -> Unit,
) {
    if (releases.isEmpty()) {
        PlansEmptyState(
            icon = {
                Icon(
                    Icons.Outlined.EventAvailable,
                    null,
                    Modifier.size(ReelsDimensions.plansEmptyIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            title = "No tracked releases",
            message = "Track an upcoming movie to get notified when it arrives.",
            actionLabel = "Find upcoming movies",
            isRelease = true,
            onBrowse = onBrowse,
        )
        return
    }
    val today = LocalDate.now()
    val (released, upcoming) =
        releases.partition { release ->
            release.releaseDate?.let {
                runCatching { LocalDate.parse(it).isBefore(today) }.getOrDefault(false)
            } == true
        }
    LazyColumn(
        contentPadding =
            PaddingValues(
                top = ReelsDimensions.plansReleaseListTopPadding,
                bottom = ReelsDimensions.plansListBottomPadding,
            )
    ) {
        items(upcoming, key = { it.movieId }) { release ->
            ReleasePlanRow(release, onMovieClick, onRemove, onEdit)
            ReleaseRowDivider()
        }
        if (released.isNotEmpty()) {
            item(key = "released-heading") { ReleasedHeading() }
            items(released, key = { "released-${it.movieId}" }) { release ->
                ReleasePlanRow(release, onMovieClick, onRemove, onEdit, isReleased = true)
                ReleaseRowDivider()
            }
        }
    }
}

@Composable
private fun ReleasePlanRow(
    release: TrackedRelease,
    onMovieClick: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onEdit: (TrackedRelease) -> Unit,
    isReleased: Boolean = false,
) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .alpha(if (isReleased) 0.75f else 1f)
                .clickable { onMovieClick(release.movieId) }
                .padding(
                    horizontal = ReelsDimensions.plansListHorizontalPadding,
                    vertical = ReelsDimensions.plansReleaseRowVerticalPadding,
                ),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            model = release.posterPath.toTmdbImageUrl(),
            contentDescription = release.title,
            modifier =
                Modifier.width(ReelsDimensions.plansPosterWidth)
                    .height(ReelsDimensions.plansPosterHeight)
                    .clip(RoundedCornerShape(ReelsDimensions.plansPosterCornerSize)),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(ReelsDimensions.plansRowSpacing))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.plansReleaseMetadataSpacing),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansActionSpacing),
            ) {
                Text(
                    release.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                release.releaseStatusLabel()?.let { status -> ReleaseStatusBadge(status) }
            }
            PlanMetadataLine(
                Icons.Outlined.CalendarMonth,
                release.releaseDateLabel(),
                highlightRelativeDate = true,
            )
            release.previousDateLabel()?.let { previous ->
                Text(
                    "Was $previous",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough,
                )
            }
            PlanMetadataLine(
                if (isReleased || !release.hasActiveReminder()) Icons.Outlined.NotificationsOff
                else Icons.Outlined.NotificationsNone,
                if (isReleased) "\u2014" else release.reminderLabel(),
                highlightIcon = !isReleased && release.hasActiveReminder(),
            )
        }
        Spacer(Modifier.width(ReelsDimensions.plansRowSpacing))
        ReleaseOverflowMenu(
            onRemove = { onRemove(release.movieId) },
            onEdit = { onEdit(release) },
            onOpenMovie = { onMovieClick(release.movieId) },
        )
    }
}

@Composable
private fun ReleaseStatusBadge(status: String) {
    val accent =
        when (status) {
            "Out today" -> ReelsTeal
            "Date moved" -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    Surface(
        shape = CircleShape,
        color =
            if (status == "Released") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else accent.copy(alpha = 0.12f),
        border =
            BorderStroke(
                1.dp,
                if (status == "Released") reelsBorderColor() else accent.copy(alpha = 0.3f),
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(ReelsDimensions.plansReleaseMetadataSpacing),
            modifier =
                Modifier.padding(
                    horizontal = ReelsDimensions.plansReleaseStatusHorizontalPadding,
                    vertical = ReelsDimensions.plansReleaseStatusVerticalPadding,
                ),
        ) {
            if (status == "Out today")
                Box(
                    Modifier.size(ReelsDimensions.plansReleaseStatusDotSize)
                        .background(accent, CircleShape)
                )
            Text(
                status,
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        fontSize = ReelsDimensions.plansReleaseStatusTextSize,
                        lineHeight = ReelsDimensions.plansReleaseStatusLineHeight,
                    ),
                fontWeight = FontWeight.SemiBold,
                color = accent,
            )
        }
    }
}

@Composable
private fun PlanMetadataLine(
    icon: ImageVector,
    text: String,
    highlightIcon: Boolean = false,
    highlightRelativeDate: Boolean = false,
) {
    val accent = MaterialTheme.colorScheme.primary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(ReelsDimensions.plansReleaseMetadataIconSpacing),
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(ReelsDimensions.plansReleaseMetadataIconSize),
            tint =
                if (highlightIcon) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text =
                buildAnnotatedString {
                    val relativeStart = if (highlightRelativeDate) text.indexOf(" · ") else -1
                    append(text)
                    if (relativeStart >= 0)
                        addStyle(SpanStyle(color = accent), relativeStart, text.length)
                },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReleaseOverflowMenu(onRemove: () -> Unit, onEdit: () -> Unit, onOpenMovie: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(ReelsDimensions.plansReleaseMenuButtonSize),
        ) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "Release options",
                modifier = Modifier.size(ReelsDimensions.plansReleaseMenuIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(ReelsDimensions.plansReleaseMenuWidth),
            shape = RoundedCornerShape(ReelsDimensions.movieNightSheetFieldCornerSize),
            containerColor = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, reelsBorderColor()),
        ) {
            DropdownMenuItem(
                text = { Text("Edit reminder", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Edit,
                        null,
                        modifier = Modifier.size(ReelsDimensions.plansReleaseMenuIconSize),
                    )
                },
                onClick = {
                    expanded = false
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = { Text("Open movie", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.OpenInNew,
                        null,
                        modifier = Modifier.size(ReelsDimensions.plansReleaseMenuIconSize),
                    )
                },
                onClick = {
                    expanded = false
                    onOpenMovie()
                },
            )
            HorizontalDivider(color = reelsBorderColor())
            DropdownMenuItem(
                text = {
                    Text(
                        "Stop tracking",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(ReelsDimensions.plansReleaseMenuIconSize),
                    )
                },
                onClick = {
                    expanded = false
                    onRemove()
                },
            )
        }
    }
}

@Composable
private fun ReleasedHeading() {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .padding(
                    horizontal = ReelsDimensions.plansListHorizontalPadding,
                    vertical = ReelsDimensions.plansReleasedHeadingVerticalPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            "RELEASED",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ReleaseRowDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
}
