/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun MovieNightPlansList(
    plans: List<MovieNightPlan>,
    onMovieClick: (Int) -> Unit,
    onEdit: (MovieNightPlan) -> Unit,
    onStart: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onFinish: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    if (plans.isEmpty()) {
        PlansEmptyState(
            icon = { Icon(Icons.Filled.CalendarMonth, null) },
            title = "No Movie Nights",
            message = "Schedule a Movie Night from a movie's details.",
        )
        return
    }
    val (pastPlans, currentPlans) = plans.partition { it.status.isPastPlan() }
    LazyColumn(contentPadding = PaddingValues(vertical = ReelsDimensions.plansMovieNightVerticalSpacing)) {
        items(currentPlans, key = { it.id }) { plan ->
            MovieNightCard(
                plan = plan,
                onMovieClick = onMovieClick,
                onEdit = onEdit,
                onStart = onStart,
                onPause = onPause,
                onResume = onResume,
                onFinish = onFinish,
                onCancel = onCancel,
                onRemove = onRemove,
            )
            Spacer(Modifier.height(ReelsDimensions.plansMovieNightVerticalSpacing))
        }
        if (pastPlans.isNotEmpty()) {
            item(key = "past-heading") { PlanSectionHeading("PAST") }
            items(pastPlans, key = { "past-${it.id}" }) { plan ->
                MovieNightCard(
                    plan = plan,
                    onMovieClick = onMovieClick,
                    onEdit = onEdit,
                    onStart = onStart,
                    onPause = onPause,
                    onResume = onResume,
                    onFinish = onFinish,
                    onCancel = onCancel,
                    onRemove = onRemove,
                )
                Spacer(Modifier.height(ReelsDimensions.plansMovieNightVerticalSpacing))
            }
        }
    }
}

@Composable
private fun MovieNightCard(
    plan: MovieNightPlan,
    onMovieClick: (Int) -> Unit,
    onEdit: (MovieNightPlan) -> Unit,
    onStart: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onFinish: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    val isJourney = plan.status == MovieNightStatus.Active || plan.status == MovieNightStatus.Paused
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsDimensions.plansListHorizontalPadding),
        shape = RoundedCornerShape(ReelsDimensions.plansMovieNightCardCornerSize),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Box {
            if (isJourney) {
                Box(Modifier.matchParentSize()) {
                    Box(
                        Modifier
                            .width(ReelsDimensions.plansJourneyAccentWidth)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ReelsDimensions.plansMovieNightCardPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onMovieClick(plan.movieId) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = plan.posterPath.toTmdbImageUrl(),
                        contentDescription = plan.title,
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
                        if (plan.status != MovieNightStatus.Upcoming) {
                            PlanStatusBadge(plan.status, plan.statusLabel())
                        }
                        Text(
                            plan.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MovieNightMetadata(Icons.Filled.CalendarMonth, plan.dateLabel())
                            MovieNightMetadata(Icons.Filled.Schedule, plan.timeLabel())
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MovieNightMetadata(Icons.Filled.Alarm, plan.reminderLabel())
                            MovieNightMetadata(Icons.Filled.EventAvailable, "${plan.runtimeMinutes / 60}h ${plan.runtimeMinutes % 60}m")
                        }
                    }
                }
                if (isJourney) {
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(plan.progressLabel(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${(plan.progressFraction() * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(5.dp))
                    LinearProgressIndicator(
                        progress = { plan.progressFraction() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ReelsDimensions.plansProgressHeight)
                            .clip(RoundedCornerShape(ReelsDimensions.plansProgressHeight)),
                    )
                }
                Spacer(Modifier.height(12.dp))
                MovieNightActions(
                    plan = plan,
                    onMovieClick = onMovieClick,
                    onEdit = onEdit,
                    onStart = onStart,
                    onPause = onPause,
                    onResume = onResume,
                    onFinish = onFinish,
                    onCancel = onCancel,
                    onRemove = onRemove,
                )
            }
        }
    }
}

@Composable
private fun PlanStatusBadge(status: MovieNightStatus, label: String) {
    val containerColor = when (status) {
        MovieNightStatus.Active,
        MovieNightStatus.StartingSoon,
        MovieNightStatus.Missed,
        -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)

        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (status) {
        MovieNightStatus.Active,
        MovieNightStatus.StartingSoon,
        MovieNightStatus.Missed,
        -> MaterialTheme.colorScheme.primary

        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(ReelsDimensions.plansStatusCornerSize),
        color = containerColor,
    ) {
        Text(
            label,
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.plansStatusHorizontalPadding,
                vertical = ReelsDimensions.plansStatusVerticalPadding,
            ),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@Composable
private fun MovieNightMetadata(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(ReelsDimensions.plansMetadataIconSize),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MovieNightActions(
    plan: MovieNightPlan,
    onMovieClick: (Int) -> Unit,
    onEdit: (MovieNightPlan) -> Unit,
    onStart: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onFinish: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansActionSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.plansActionSpacing),
    ) {
        when (plan.status) {
            MovieNightStatus.Upcoming -> {
                OutlinedButton(onClick = { onEdit(plan) }, modifier = Modifier.weight(1f)) { Text("Edit") }
                TextButton(onClick = { onCancel(plan.id) }) { Text("Cancel") }
            }
            MovieNightStatus.StartingSoon -> {
                Button(onClick = { onStart(plan.id) }, modifier = Modifier.weight(1f)) { Text("Start now") }
                OutlinedButton(onClick = { onEdit(plan) }) { Text("Edit") }
                TextButton(onClick = { onCancel(plan.id) }) { Text("Cancel") }
            }
            MovieNightStatus.Active -> {
                OutlinedButton(onClick = { onFinish(plan.id) }, modifier = Modifier.weight(1f)) { Text("Mark watched") }
                OutlinedButton(onClick = { onPause(plan.id) }) { Text("Pause") }
            }
            MovieNightStatus.Paused -> {
                OutlinedButton(onClick = { onResume(plan.id) }, modifier = Modifier.weight(1f)) { Text("Resume") }
                TextButton(onClick = { onCancel(plan.id) }) { Text("End night") }
            }
            MovieNightStatus.Completed -> {
                OutlinedButton(onClick = { onMovieClick(plan.movieId) }, modifier = Modifier.weight(1f)) { Text("Open movie") }
                TextButton(onClick = { onRemove(plan.id) }) { Text("Dismiss") }
            }
            MovieNightStatus.Missed -> {
                OutlinedButton(onClick = { onEdit(plan) }, modifier = Modifier.weight(1f)) { Text("Reschedule") }
                TextButton(onClick = { onRemove(plan.id) }) { Text("Dismiss") }
            }
            MovieNightStatus.Cancelled -> {
                OutlinedButton(onClick = { onRemove(plan.id) }, modifier = Modifier.fillMaxWidth()) { Text("Dismiss") }
            }
        }
    }
}

@Composable
private fun PlanSectionHeading(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.plansListHorizontalPadding,
                vertical = ReelsDimensions.plansMovieNightVerticalSpacing,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
    ) {
        androidx.compose.material3.HorizontalDivider(modifier = Modifier.weight(1f))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        androidx.compose.material3.HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

private fun MovieNightStatus.isPastPlan(): Boolean = when (this) {
    MovieNightStatus.Completed,
    MovieNightStatus.Missed,
    MovieNightStatus.Cancelled,
    -> true
    else -> false
}
