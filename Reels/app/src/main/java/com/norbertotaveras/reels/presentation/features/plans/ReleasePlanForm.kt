/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReleasePlanForm(
    movie: MovieDetailsDataModel,
    trackedRelease: TrackedRelease?,
    isSaving: Boolean,
    canScheduleExactAlarms: Boolean,
    canPostNotifications: Boolean,
    onRequestExactAlarms: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (ReleaseReminder, Long?, Boolean) -> Unit,
    onRemove: () -> Unit,
) {
    val existingCustomDateTime = trackedRelease?.customReminderAt
        ?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()) }
    val releaseDate = movie.releaseDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    var reminder by rememberSaveable(trackedRelease?.reminder) {
        mutableStateOf(trackedRelease?.reminder ?: ReleaseReminder.AppDefault)
    }
    var customDate by rememberSaveable(trackedRelease?.movieId) {
        mutableStateOf(existingCustomDateTime?.toLocalDate() ?: releaseDate?.minusDays(1) ?: LocalDate.now().plusDays(1))
    }
    var customTime by rememberSaveable(trackedRelease?.movieId) {
        mutableStateOf(existingCustomDateTime?.toLocalTime()?.withSecond(0)?.withNano(0) ?: LocalTime.of(9, 0))
    }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    val customReminderAt = customDate.atTime(customTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val isTracked = trackedRelease != null

    PlanSheetHeader(title = "Release reminder", onDismiss = onDismiss)
    PlanSheetDivider()
    MoviePlanSummary(
        title = movie.title,
        posterPath = movie.posterPath,
        subtitle = releaseDate.releaseSummary(),
    )
    PlanSheetDivider()

    Column(
        modifier = Modifier.padding(
            horizontal = ReelsDimensions.planSheetHorizontalPadding,
            vertical = ReelsDimensions.planSheetSectionVerticalPadding,
        )
    ) {
        Text(
            text = "REMINDER",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        releaseReminderOptions.forEach { option ->
            ReleaseReminderRow(
                option = option,
                selected = reminder == option.value,
                onClick = { reminder = option.value },
            )
        }
        if (reminder == ReleaseReminder.Custom) {
            Row(
                modifier = Modifier.padding(top = ReelsDimensions.planSheetContentSpacing),
                horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.planSheetContentSpacing),
            ) {
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(customDate.format(planInputDateFormatter))
                }
                OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.weight(1f)) {
                    Text(customTime.format(planInputTimeFormatter))
                }
            }
        }
        PlanPermissionNotice(canPostNotifications = canPostNotifications)
        if (!canScheduleExactAlarms) {
            PreciseAlarmNotice(canScheduleExactAlarms, onRequestExactAlarms)
        }
    }

    PlanSheetDivider()
    Column(
        modifier = Modifier.padding(
            horizontal = ReelsDimensions.planSheetHorizontalPadding,
            vertical = ReelsDimensions.planSheetSectionVerticalPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.planSheetContentSpacing),
    ) {
        Button(
            onClick = {
                onSave(
                    reminder,
                    customReminderAt.takeIf { reminder == ReleaseReminder.Custom },
                    true,
                )
            },
            enabled = !isSaving && (reminder != ReleaseReminder.Custom || customReminderAt > System.currentTimeMillis()),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(ReelsDimensions.planSheetPrimaryActionHeight),
            shape = RoundedCornerShape(ReelsDimensions.planSheetPrimaryActionCornerSize),
        ) {
            Text(if (isTracked) "Save changes" else "Track release")
        }
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Text("Cancel")
        }
        if (isTracked) {
            TextButton(onClick = onRemove, enabled = !isSaving, modifier = Modifier.fillMaxWidth()) {
                Text("Stop tracking", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = customDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        customDate = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) { DatePicker(state = pickerState) }
    }
    if (showTimePicker) {
        val pickerState = rememberTimePickerState(customTime.hour, customTime.minute)
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select time") },
            confirmButton = {
                TextButton(onClick = {
                    customTime = LocalTime.of(pickerState.hour, pickerState.minute)
                    showTimePicker = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
        ) { TimeInput(state = pickerState) }
    }
}

@Composable
internal fun PlanSheetHeader(title: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.planSheetHorizontalPadding,
                vertical = ReelsDimensions.planSheetHeaderVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Surface(
            modifier = Modifier.size(ReelsDimensions.planSheetCloseButtonSize),
            shape = RoundedCornerShape(ReelsDimensions.planSheetChoiceIconCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                )
            }
        }
    }
}

@Composable
internal fun MoviePlanSummary(title: String, posterPath: String?, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.planSheetHorizontalPadding,
                vertical = ReelsDimensions.planSheetSectionVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
    ) {
        AsyncImage(
            model = posterPath.toTmdbImageUrl(),
            contentDescription = title,
            modifier = Modifier
                .width(ReelsDimensions.planSheetPosterWidth)
                .height(ReelsDimensions.planSheetPosterHeight)
                .clip(RoundedCornerShape(ReelsDimensions.planSheetPosterCornerSize)),
            contentScale = ContentScale.Crop,
        )
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
internal fun PlanSheetDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
}

@Composable
private fun ReleaseReminderRow(
    option: ReleaseReminderOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = ReelsDimensions.planSheetOptionVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
    ) {
        Icon(
            imageVector = if (selected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(ReelsDimensions.planSheetOptionIndicatorSize),
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(option.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            option.subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
}

@Composable
internal fun PlanPermissionNotice(
    canPostNotifications: Boolean,
    canScheduleExactAlarms: Boolean = true,
    modifier: Modifier = Modifier.padding(top = ReelsDimensions.planSheetContentSpacing),
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
) {
    if (canPostNotifications && canScheduleExactAlarms) return
    val message = when {
        !canPostNotifications && !canScheduleExactAlarms ->
            "Reels needs notification permission to send the reminder. " +
                "Exact alarm timing on Android 12+ may require an additional permission from " +
                "Settings \u2192 Apps \u2192 Reels \u2192 Alarms & Reminders."
        !canPostNotifications ->
            "Reels needs notification permission to send the reminder."
        else ->
            "Exact alarm timing on Android 12+ may require an additional permission from " +
                "Settings \u2192 Apps \u2192 Reels \u2192 Alarms & Reminders."
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ReelsDimensions.planSheetNoticeCornerSize),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(ReelsDimensions.planSheetNoticePadding),
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
        ) {
            Icon(
                Icons.Filled.NotificationsNone,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun LocalDate?.releaseSummary(): String {
    if (this == null) return "Release date: TBA"
    val days = ChronoUnit.DAYS.between(LocalDate.now(), this)
    val relative = when (days) {
        0L -> "today"
        1L -> "1 day away"
        else -> if (days > 1) "$days days away" else "released"
    }
    return "Release date: ${format(planReleaseDateFormatter)} · $relative"
}
