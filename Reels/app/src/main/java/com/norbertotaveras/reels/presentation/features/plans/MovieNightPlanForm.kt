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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MovieNightPlanForm(
    movieTitle: String,
    posterPath: String?,
    initialRuntimeMinutes: Int?,
    isSaving: Boolean,
    canScheduleExactAlarms: Boolean,
    canPostNotifications: Boolean,
    onDismiss: () -> Unit,
    onSave: (Long, Int, Int?, String?, Int?, String?) -> Unit,
    existingPlan: MovieNightPlan? = null,
    errorMessage: String? = null,
) {
    val initialDateTime =
        existingPlan?.scheduledAt?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()) }
    var date by
        rememberSaveable(existingPlan?.id) {
            mutableStateOf(initialDateTime?.toLocalDate() ?: LocalDate.now().plusDays(1))
        }
    var time by
        rememberSaveable(existingPlan?.id) {
            mutableStateOf(
                initialDateTime?.toLocalTime()?.withSecond(0)?.withNano(0) ?: LocalTime.of(20, 0)
            )
        }
    var runtime by
        rememberSaveable(existingPlan?.id) {
            mutableStateOf(
                (existingPlan?.runtimeMinutes ?: initialRuntimeMinutes?.takeIf { it in 1..600 })
                    ?.toString()
                    .orEmpty()
            )
        }
    var reminder by
        rememberSaveable(existingPlan?.id) {
            mutableStateOf(existingPlan?.reminderMinutesBefore ?: 30)
        }
    var reminderEnabled by
        rememberSaveable(existingPlan?.id) {
            mutableStateOf(existingPlan == null || existingPlan.reminderMinutesBefore != null)
        }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    val runtimeValue = runtime.toIntOrNull()
    val tmdbRuntime = initialRuntimeMinutes?.takeIf { it in 1..600 }
    val scheduledAt = date.atTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val isValid = runtimeValue in 1..600 && scheduledAt > System.currentTimeMillis()
    val maxHeight =
        with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() } *
            ReelsDimensions.movieNightSheetMaxHeightFraction -
            ReelsDimensions.movieNightSheetHandleTopPadding -
            ReelsDimensions.movieNightSheetHandleHeight -
            ReelsDimensions.movieNightSheetHandleBottomPadding
    val hasFixedSummary = maxHeight >= ReelsDimensions.movieNightSheetCompactHeight

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight)) {
        PlanSheetHeader(
            title = if (existingPlan == null) "Movie night" else "Edit movie night",
            onDismiss = onDismiss,
        )
        PlanSheetDivider()
        if (hasFixedSummary) {
            MoviePlanSummary(
                title = movieTitle,
                posterPath = posterPath,
                subtitle = tmdbRuntime?.toDurationLabel() ?: "Runtime required",
            )
            PlanSheetDivider()
        }

        Column(
            modifier =
                Modifier.weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = ReelsDimensions.planSheetHorizontalPadding,
                        vertical = ReelsDimensions.movieNightSheetBodyPadding,
                    ),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.planSheetContentSpacing),
        ) {
            if (!hasFixedSummary) {
                Text(
                    movieTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            PlanDateTimeField(
                label = "Date",
                value = date.format(planInputDateFormatter),
                icon = Icons.Filled.CalendarMonth,
                onClick = { showDatePicker = true },
            )
            PlanDateTimeField(
                label = "Start time",
                value = time.format(planInputTimeFormatter),
                icon = Icons.Filled.Schedule,
                onClick = { showTimePicker = true },
            )
            if (scheduledAt <= System.currentTimeMillis()) {
                Text(
                    "Choose a future date and time.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
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
                Surface(
                    shape = RoundedCornerShape(ReelsDimensions.movieNightSheetFieldCornerSize),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                ) {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(
                                    horizontal = ReelsDimensions.planSheetFieldHorizontalPadding,
                                    vertical = ReelsDimensions.planSheetOptionVerticalPadding,
                                ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
                    ) {
                        Icon(
                            Icons.Filled.Schedule,
                            null,
                            modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Runtime",
                                style =
                                    MaterialTheme.typography.bodySmall.copy(
                                        fontSize = ReelsDimensions.movieNightSheetRuntimeLabelSize,
                                        lineHeight =
                                            ReelsDimensions.movieNightSheetRuntimeLabelLineHeight,
                                    ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                tmdbRuntime.toDurationLabel(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                        Text(
                            "From TMDB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(ReelsDimensions.movieNightSheetFieldLabelSpacing)
            ) {
                Text(
                    "Reminder before start",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansActionSpacing),
                ) {
                    movieNightReminderOptions.forEach { (value, label) ->
                        PlanChoiceChip(
                            label = label,
                            selected = reminderEnabled && reminder == value,
                            onClick = {
                                reminder = value
                                reminderEnabled = true
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    PlanChoiceChip(
                        label = "Off",
                        selected = !reminderEnabled,
                        onClick = { reminderEnabled = false },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            PlanPermissionNotice(
                canPostNotifications = canPostNotifications,
                canScheduleExactAlarms = canScheduleExactAlarms,
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            )
            errorMessage?.let { message ->
                Text(
                    message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        PlanSheetDivider()
        Column(
            modifier =
                Modifier.padding(
                    start = ReelsDimensions.planSheetHorizontalPadding,
                    end = ReelsDimensions.planSheetHorizontalPadding,
                    top = ReelsDimensions.planSheetSectionVerticalPadding,
                    bottom = ReelsDimensions.movieNightSheetFooterBottomPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.movieNightSheetActionSpacing),
        ) {
            Button(
                onClick = {
                    onSave(
                        scheduledAt,
                        checkNotNull(runtimeValue),
                        reminder.takeIf { reminderEnabled },
                        existingPlan?.venue,
                        existingPlan?.attendeeCount,
                        existingPlan?.note,
                    )
                },
                enabled = isValid && !isSaving,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background,
                    ),
                modifier =
                    Modifier.fillMaxWidth().height(ReelsDimensions.planSheetPrimaryActionHeight),
                shape = RoundedCornerShape(ReelsDimensions.movieNightSheetFieldCornerSize),
            ) {
                Text(
                    if (existingPlan == null) "Schedule movie night" else "Save changes",
                    fontWeight = FontWeight.SemiBold,
                )
            }
            TextButton(
                onClick = onDismiss,
                modifier =
                    Modifier.fillMaxWidth().height(ReelsDimensions.movieNightSheetCancelHeight),
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
            ) {
                Text("Cancel")
            }
        }
    }

    if (showDatePicker) {
        val pickerState =
            rememberDatePickerState(
                initialSelectedDateMillis =
                    date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
            )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            date = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Done")
                }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) {
            DatePicker(
                state = pickerState,
                colors =
                    DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        selectedDayContentColor = MaterialTheme.colorScheme.background,
                        selectedYearContentColor = MaterialTheme.colorScheme.background,
                    ),
            )
        }
    }
    if (showTimePicker) {
        val pickerState = rememberTimePickerState(time.hour, time.minute)
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Select time") },
            confirmButton = {
                TextButton(
                    onClick = {
                        time = LocalTime.of(pickerState.hour, pickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("Done")
                }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
        ) {
            TimeInput(
                state = pickerState,
                colors =
                    TimePickerDefaults.colors(
                        timeSelectorSelectedContainerColor =
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.background,
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                        periodSelectorSelectedContainerColor =
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        periodSelectorUnselectedContainerColor =
                            MaterialTheme.colorScheme.background,
                        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
                        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        }
    }
}

@Composable
private fun PlanDateTimeField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.movieNightSheetFieldLabelSpacing)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            modifier =
                Modifier.fillMaxWidth()
                    .height(ReelsDimensions.movieNightSheetFieldHeight)
                    .clickable(onClick = onClick),
            shape = RoundedCornerShape(ReelsDimensions.movieNightSheetFieldCornerSize),
            color = MaterialTheme.colorScheme.background,
            border =
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                ),
        ) {
            Row(
                modifier =
                    Modifier.padding(horizontal = ReelsDimensions.planSheetFieldHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
            ) {
                Icon(
                    icon,
                    null,
                    modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun PlanChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(ReelsDimensions.planSheetFieldCornerSize),
        color =
            if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.background
            },
        border =
            androidx.compose.foundation.BorderStroke(
                1.dp,
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.outlineVariant,
            ),
    ) {
        Row(
            modifier =
                Modifier.padding(vertical = ReelsDimensions.movieNightSheetChipVerticalPadding),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color =
                    if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun Int.toDurationLabel(): String = "${this / 60}h ${this % 60}m"
