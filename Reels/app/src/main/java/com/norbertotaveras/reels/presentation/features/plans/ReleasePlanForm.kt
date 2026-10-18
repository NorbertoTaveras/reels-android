/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease

@Composable
internal fun ReleasePlanForm(
    movie: MovieDetailsDataModel,
    trackedRelease: TrackedRelease?,
    isSaving: Boolean,
    canScheduleExactAlarms: Boolean,
    canPostNotifications: Boolean,
    onRequestExactAlarms: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (ReleaseReminder, Boolean) -> Unit,
    onRemove: () -> Unit,
) {
    var reminder by rememberSaveable(trackedRelease?.reminder) {
        mutableStateOf(
            trackedRelease?.reminder
                ?.takeUnless { it == ReleaseReminder.AppDefault }
                ?: ReleaseReminder.OneDayBefore
        )
    }
    var notificationsEnabled by rememberSaveable(trackedRelease?.notificationsEnabled) {
        mutableStateOf(trackedRelease?.notificationsEnabled ?: true)
    }
    val isTracked = trackedRelease != null

    Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(
            if (isTracked) "Release reminder" else "Track release",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Text(
        movie.releaseDate ?: "Release date TBA. A reminder will be scheduled after TMDB provides a complete date.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text("Notifications", fontWeight = FontWeight.Medium)
            Text("Release-date updates and reminders", style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
    }
    Text("Remind me", style = MaterialTheme.typography.titleSmall)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        releaseReminderOptions.forEach { (value, label) ->
            FilterChip(
                selected = reminder == value,
                onClick = { reminder = value },
                label = { Text(label) },
            )
        }
    }
    PreciseAlarmNotice(canScheduleExactAlarms, onRequestExactAlarms)
    NotificationPermissionNotice(canPostNotifications)
    Button(
        onClick = { onSave(reminder, notificationsEnabled) },
        enabled = !isSaving,
        modifier = Modifier.fillMaxWidth(),
    ) { Text(if (isTracked) "Save changes" else "Track release") }
    if (isTracked) {
        TextButton(onClick = onRemove, enabled = !isSaving, modifier = Modifier.fillMaxWidth()) {
            Text("Stop tracking", color = MaterialTheme.colorScheme.error)
        }
    }
}

private val releaseReminderOptions = listOf(
    ReleaseReminder.OnReleaseDay to "Release day",
    ReleaseReminder.OneDayBefore to "1 day before",
    ReleaseReminder.OneWeekBefore to "1 week before",
    ReleaseReminder.None to "No reminder",
)
