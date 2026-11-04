/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.release

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.plans.model.ReleaseReminder
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease
import com.norbertotaveras.reels.presentation.features.plans.shared.PlanDateTimeField
import com.norbertotaveras.reels.presentation.features.plans.shared.ReleaseReminderOption
import com.norbertotaveras.reels.presentation.features.plans.shared.planInputDateFormatter
import com.norbertotaveras.reels.presentation.features.plans.shared.planInputTimeFormatter
import com.norbertotaveras.reels.presentation.features.plans.shared.planReleaseDateFormatter
import com.norbertotaveras.reels.presentation.features.plans.shared.releaseReminderOptions
import com.norbertotaveras.reels.presentation.ui.extensions.defaultReminderLabel
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
    movieTitle: String,
    posterPath: String?,
    initialReleaseDate: String?,
    trackedRelease: TrackedRelease?,
    isSaving: Boolean,
    canScheduleExactAlarms: Boolean,
    canPostNotifications: Boolean,
    onDismiss: () -> Unit,
    onSave: (ReleaseReminder, Long?, Boolean) -> Unit,
    errorMessage: String?,
    defaultReminder: ReleaseReminder = ReleaseReminder.OneDayBefore,
) {
    val existingCustomDateTime =
        trackedRelease?.customReminderAt?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
        }
    val releaseDate =
        (trackedRelease?.releaseDate ?: initialReleaseDate)?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }
    var reminder by
        rememberSaveable(trackedRelease?.reminder) {
            mutableStateOf(trackedRelease?.reminder ?: ReleaseReminder.AppDefault)
        }
    var customDate by
        rememberSaveable(trackedRelease?.movieId) {
            mutableStateOf(
                existingCustomDateTime?.toLocalDate()
                    ?: releaseDate?.minusDays(1)
                    ?: LocalDate.now().plusDays(1)
            )
        }
    var customTime by
        rememberSaveable(trackedRelease?.movieId) {
            mutableStateOf(
                existingCustomDateTime?.toLocalTime()?.withSecond(0)?.withNano(0)
                    ?: LocalTime.of(9, 0)
            )
        }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    val customReminderAt =
        customDate.atTime(customTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val isTracked = trackedRelease != null
    val maxHeight =
        with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() } *
            ReelsDimensions.movieNightSheetMaxHeightFraction -
            ReelsDimensions.movieNightSheetHandleTopPadding -
            ReelsDimensions.movieNightSheetHandleHeight -
            ReelsDimensions.movieNightSheetHandleBottomPadding

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight)) {
        PlanSheetHeader(title = "Release reminder", onDismiss = onDismiss)
        PlanSheetDivider()
        MoviePlanSummary(
            title = movieTitle,
            posterPath = posterPath,
            subtitle = releaseDate.releaseSummary(),
        )
        PlanSheetDivider()

        Column(
            modifier =
                Modifier.weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = ReelsDimensions.planSheetHorizontalPadding,
                        vertical = ReelsDimensions.releaseSheetBodyVerticalPadding,
                    )
        ) {
            Text(
                text = "REMINDER",
                modifier =
                    Modifier.padding(
                            horizontal = ReelsDimensions.releaseSheetOptionHorizontalPadding
                        )
                        .padding(bottom = ReelsDimensions.releaseSheetLabelBottomSpacing),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            releaseReminderOptions.forEachIndexed { index, baseOption ->
                val option =
                    if (baseOption.value == ReleaseReminder.AppDefault) {
                        baseOption.copy(
                            subtitle =
                                "${defaultReminder.defaultReminderLabel()} at 9:00 AM (app setting)"
                        )
                    } else baseOption
                ReleaseReminderRow(
                    option = option,
                    selected = reminder == option.value,
                    onClick = { reminder = option.value },
                )
                if (index < releaseReminderOptions.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                }
            }
            if (reminder == ReleaseReminder.Custom) {
                Column(
                    modifier = Modifier.padding(top = ReelsDimensions.planSheetContentSpacing),
                    verticalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
                ) {
                    PlanDateTimeField(
                        label = "Date",
                        value = customDate.format(planInputDateFormatter),
                        icon = Icons.Filled.CalendarMonth,
                        onClick = { showDatePicker = true },
                        labelSpacing = ReelsDimensions.releaseSheetLabelBottomSpacing,
                    )
                    PlanDateTimeField(
                        label = "Time",
                        value = customTime.format(planInputTimeFormatter),
                        icon = Icons.Filled.Schedule,
                        onClick = { showTimePicker = true },
                        labelSpacing = ReelsDimensions.releaseSheetLabelBottomSpacing,
                    )
                }
            }
            PlanPermissionNotice(
                canPostNotifications = canPostNotifications,
                canScheduleExactAlarms = canScheduleExactAlarms,
                modifier = Modifier.padding(top = ReelsDimensions.releaseSheetNoticeTopSpacing),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            )
            errorMessage?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
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
                        reminder,
                        customReminderAt.takeIf { reminder == ReleaseReminder.Custom },
                        true,
                    )
                },
                enabled =
                    !isSaving &&
                        (reminder != ReleaseReminder.Custom ||
                            customReminderAt > System.currentTimeMillis()),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background,
                    ),
                modifier =
                    Modifier.fillMaxWidth().height(ReelsDimensions.planSheetPrimaryActionHeight),
                shape = RoundedCornerShape(ReelsDimensions.movieNightSheetFieldCornerSize),
            ) {
                Text(if (isTracked) "Save changes" else "Track release")
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
                    customDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
            )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            customDate =
                                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
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
        val pickerState = rememberTimePickerState(customTime.hour, customTime.minute)
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Select time") },
            confirmButton = {
                TextButton(
                    onClick = {
                        customTime = LocalTime.of(pickerState.hour, pickerState.minute)
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
internal fun PlanSheetHeader(title: String, onDismiss: () -> Unit) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .padding(
                    horizontal = ReelsDimensions.planSheetHorizontalPadding,
                    vertical = ReelsDimensions.planSheetHeaderVerticalPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style =
                MaterialTheme.typography.titleMedium.copy(
                    lineHeight = ReelsDimensions.planSheetChoiceTitleLineHeight
                ),
            fontWeight = FontWeight.Medium,
        )
        Surface(
            modifier = Modifier.size(ReelsDimensions.planSheetCloseButtonSize),
            shape = RoundedCornerShape(ReelsDimensions.planSheetChoiceCloseCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun MoviePlanSummary(title: String, posterPath: String?, subtitle: String) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
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
            modifier =
                Modifier.width(ReelsDimensions.planSheetPosterWidth)
                    .height(ReelsDimensions.planSheetPosterHeight)
                    .clip(RoundedCornerShape(ReelsDimensions.planSheetPosterCornerSize)),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
        modifier =
            Modifier.fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(
                    vertical = ReelsDimensions.planSheetOptionVerticalPadding,
                    horizontal = ReelsDimensions.releaseSheetOptionHorizontalPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
    ) {
        Icon(
            imageVector =
                if (selected) Icons.Filled.RadioButtonChecked
                else Icons.Filled.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(ReelsDimensions.planSheetOptionIndicatorSize),
            tint =
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.planSheetHeaderSpacing),
        ) {
            Text(
                option.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            )
            option.subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun PlanPermissionNotice(
    canPostNotifications: Boolean,
    modifier: Modifier = Modifier,
    canScheduleExactAlarms: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
) {
    if (canPostNotifications && canScheduleExactAlarms) return
    val message =
        when {
            !canPostNotifications && !canScheduleExactAlarms ->
                "Reels needs notification permission to send the reminder. " +
                    "Exact alarm timing on Android 12+ may require an additional permission from " +
                    "Settings \u2192 Apps \u2192 Reels \u2192 Alarms & Reminders."
            !canPostNotifications -> "Reels needs notification permission to send the reminder."
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
    val relative =
        when (days) {
            0L -> "today"
            1L -> "1 day away"
            else -> if (days > 1) "$days days away" else "released"
        }
    return "Release date: ${format(planReleaseDateFormatter)} · $relative"
}
