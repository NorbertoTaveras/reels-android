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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import coil3.compose.AsyncImage
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieNightPlanForm(
    movieTitle: String,
    posterPath: String?,
    initialRuntimeMinutes: Int?,
    isSaving: Boolean,
    canScheduleExactAlarms: Boolean,
    canPostNotifications: Boolean,
    onRequestExactAlarms: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (Long, Int, Int?, String?, Int?, String?) -> Unit,
    existingPlan: MovieNightPlan? = null,
) {
    val initialDateTime = remember(existingPlan?.scheduledAt) {
        existingPlan?.scheduledAt
            ?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()) }
    }
    var date by rememberSaveable(existingPlan?.id) {
        mutableStateOf(initialDateTime?.toLocalDate() ?: LocalDate.now().plusDays(1))
    }
    var time by rememberSaveable(existingPlan?.id) {
        mutableStateOf(initialDateTime?.toLocalTime()?.withSecond(0)?.withNano(0) ?: LocalTime.of(19, 0))
    }
    var runtime by rememberSaveable(existingPlan?.id) {
        mutableStateOf((existingPlan?.runtimeMinutes ?: initialRuntimeMinutes?.takeIf { it in 1..600 })?.toString().orEmpty())
    }
    var reminder by rememberSaveable(existingPlan?.id) { mutableStateOf(existingPlan?.reminderMinutesBefore ?: 30) }
    var reminderEnabled by rememberSaveable(existingPlan?.id) {
        mutableStateOf(existingPlan == null || existingPlan.reminderMinutesBefore != null)
    }
    var venue by rememberSaveable(existingPlan?.id) { mutableStateOf(existingPlan?.venue.orEmpty()) }
    var attendees by rememberSaveable(existingPlan?.id) { mutableStateOf(existingPlan?.attendeeCount?.toString().orEmpty()) }
    var note by rememberSaveable(existingPlan?.id) { mutableStateOf(existingPlan?.note.orEmpty()) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    val runtimeValue = runtime.toIntOrNull()
    val tmdbRuntime = initialRuntimeMinutes?.takeIf { it in 1..600 }
    val attendeeValue = attendees.toIntOrNull()
    val scheduledAt = date.atTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val isValid = runtimeValue in 1..600 &&
        (attendees.isBlank() || attendeeValue != null && attendeeValue > 0) &&
        scheduledAt > System.currentTimeMillis()

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            if (existingPlan == null) "Movie night" else "Edit movie night",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Close") }
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AsyncImage(
            model = posterPath.toTmdbImageUrl(),
            contentDescription = movieTitle,
            modifier = Modifier.width(40.dp).height(58.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(movieTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            tmdbRuntime?.let { minutes ->
                Text("${minutes / 60}h ${minutes % 60}m", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Date", style = MaterialTheme.typography.labelMedium)
            OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text(date.format(movieNightDateFormatter))
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Start time", style = MaterialTheme.typography.labelMedium)
            OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text(time.format(movieNightTimeFormatter))
            }
        }
    }
    if (scheduledAt <= System.currentTimeMillis()) {
        Text("Choose a future date and time.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
    if (tmdbRuntime == null) {
        OutlinedTextField(
            value = runtime,
            onValueChange = { runtime = it.filter(Char::isDigit).take(3) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Runtime (minutes)") },
            supportingText = { Text("Required, from 1 to 600 minutes") },
            isError = runtime.isNotBlank() && runtimeValue !in 1..600,
            singleLine = true,
        )
    } else {
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Filled.Schedule, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(Modifier.weight(1f)) {
                    Text("Runtime", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${tmdbRuntime / 60}h ${tmdbRuntime % 60}m", style = MaterialTheme.typography.titleSmall)
                }
                Text("From TMDB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    Text("Reminder", style = MaterialTheme.typography.titleSmall)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        movieNightReminderOptions.forEach { (value, label) ->
            FilterChip(
                selected = reminderEnabled && reminder == value,
                onClick = { reminder = value; reminderEnabled = true },
                label = { Text(label) },
            )
        }
        FilterChip(selected = !reminderEnabled, onClick = { reminderEnabled = false }, label = { Text("Off") })
    }
    HorizontalDivider()
    OutlinedTextField(
        value = venue,
        onValueChange = { venue = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Venue (optional)") },
        singleLine = true,
    )
    OutlinedTextField(
        value = attendees,
        onValueChange = { attendees = it.filter(Char::isDigit).take(3) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Attendees (optional)") },
        isError = attendees.isNotBlank() && (attendeeValue == null || attendeeValue < 1),
        singleLine = true,
    )
    OutlinedTextField(
        value = note,
        onValueChange = { note = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Note (optional)") },
        minLines = 2,
    )
    PreciseAlarmNotice(canScheduleExactAlarms, onRequestExactAlarms)
    NotificationPermissionNotice(canPostNotifications)
    Button(
        onClick = {
            onSave(scheduledAt, checkNotNull(runtimeValue), reminder.takeIf { reminderEnabled }, venue, attendeeValue, note)
        },
        enabled = isValid && !isSaving,
        modifier = Modifier.fillMaxWidth(),
    ) { Text(if (existingPlan == null) "Schedule" else "Save changes") }
    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) { DatePicker(state = pickerState) }
    }
    if (showTimePicker) {
        val pickerState = rememberTimePickerState(time.hour, time.minute)
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select time") },
            confirmButton = {
                TextButton(onClick = {
                    time = LocalTime.of(pickerState.hour, pickerState.minute)
                    showTimePicker = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
        ) { TimeInput(state = pickerState) }
    }
}

private val movieNightReminderOptions = listOf(
    15 to "15 min",
    30 to "30 min",
    60 to "1 hour",
    120 to "2 hr",
)

private val movieNightDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
private val movieNightTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
