/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.presentation.ui.extensions.defaultReminderLabel
import com.norbertotaveras.reels.presentation.ui.extensions.movieNightReminderLabel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun SettingsReminderDialogs(state: SettingsUiState, onEvent: (SettingsEvent) -> Unit) {
    val dismiss = { onEvent(SettingsEvent.PreferenceDialogDismissed) }
    when (state.activePreferenceDialog) {
        SettingsPreferenceDialog.ReleaseReminder ->
            SettingsOptionDialog(
                title = "Default release reminder",
                description =
                    "Applied to new tracked releases. Individual plans can override this.",
                icon = Icons.Outlined.CalendarMonth,
                options =
                    listOf(
                        ReleaseReminder.OnReleaseDay,
                        ReleaseReminder.OneDayBefore,
                        ReleaseReminder.OneWeekBefore,
                    ),
                selectedOption = state.planPreferences.releaseReminder,
                optionLabel = { it.defaultReminderLabel() },
                optionDescription = {
                    when (it) {
                        ReleaseReminder.OnReleaseDay -> "9:00 AM on the day of release"
                        ReleaseReminder.OneWeekBefore -> "9:00 AM, 7 days in advance"
                        else -> "9:00 AM the day before"
                    }
                },
                confirmLabel = "Save",
                onOptionSelected = { onEvent(SettingsEvent.ReleaseReminderSelected(it)) },
                onDismiss = dismiss,
            )
        SettingsPreferenceDialog.MovieNightReminder ->
            SettingsOptionDialog(
                title = "Default Movie Night reminder",
                description = "Applied to new movie nights. Individual plans can override this.",
                icon = Icons.Outlined.Schedule,
                options = listOf(15, 30, 60, 120, null),
                selectedOption = state.planPreferences.movieNightReminderMinutes,
                optionLabel = { it.movieNightReminderLabel() },
                optionDescription = { "" },
                confirmLabel = "Save",
                onOptionSelected = { onEvent(SettingsEvent.MovieNightReminderSelected(it)) },
                onDismiss = dismiss,
            )
        SettingsPreferenceDialog.Notifications ->
            SettingsDialog(
                icon = Icons.Outlined.NotificationsNone,
                title =
                    if (state.canPostNotifications) "Notifications" else "Turn on notifications",
                onDismiss = dismiss,
                dismissLabel = "Not now",
                confirmLabel = if (state.canPostNotifications) "Open settings" else "Allow",
                onConfirm = { onEvent(SettingsEvent.NotificationAccessRequested) },
            ) {
                ReminderDialogBody(
                    description = "Reels uses notifications for:",
                    footer = "You can change notification preferences any time in system settings.",
                ) {
                    ReminderFeatureRow(
                        Icons.Outlined.CalendarMonth,
                        "Release alerts",
                        "Reminders for the movies you track",
                    )
                    ReminderFeatureRow(
                        Icons.Outlined.Movie,
                        "Movie Night reminders",
                        "Reminder before your scheduled screenings",
                    )
                    ReminderFeatureRow(
                        Icons.Outlined.Schedule,
                        "Movie Night progress",
                        "Countdown and runtime progress for your movie night",
                    )
                }
            }
        SettingsPreferenceDialog.PreciseReminders ->
            SettingsDialog(
                icon = Icons.Outlined.Alarm,
                title = "Exact alarm access",
                onDismiss = dismiss,
                dismissLabel = "Not now",
                confirmLabel =
                    if (state.canScheduleExactAlarms) "Open settings" else "Grant access",
                onConfirm = { onEvent(SettingsEvent.PreciseReminderAccessRequested) },
            ) {
                ReminderDialogBody(
                    description = "Without exact alarm permission, Android may delay reminders.",
                    footer =
                        "Reels uses this access for your scheduled reminders and Movie Night progress.",
                ) {
                    ReminderFeatureRow(
                        Icons.Outlined.Check,
                        "With exact access",
                        "Reminders are scheduled for your chosen time, such as 9:00 AM.",
                    )
                    ReminderFeatureRow(
                        Icons.Outlined.WarningAmber,
                        "Without exact access",
                        "Android batches timers and may deliver reminders later.",
                    )
                }
            }
        SettingsPreferenceDialog.LiveUpdates ->
            SettingsDialog(
                icon = Icons.Outlined.Bolt,
                title = "Live Updates",
                onDismiss = dismiss,
                dismissLabel =
                    if (state.supportsLiveUpdates) {
                        if (state.planPreferences.liveUpdatesEnabled) "Disable" else "Enable"
                    } else null,
                confirmLabel =
                    if (state.supportsLiveUpdates && !state.canPromoteLiveUpdates) "Open settings"
                    else "Got it",
                onConfirm = {
                    if (state.supportsLiveUpdates && !state.canPromoteLiveUpdates)
                        onEvent(SettingsEvent.LiveUpdateAccessRequested)
                    else dismiss()
                },
                onDismissAction = {
                    onEvent(
                        SettingsEvent.LiveUpdatesChanged(!state.planPreferences.liveUpdatesEnabled)
                    )
                },
            ) {
                ReminderDialogBody(
                    description =
                        "Live Updates show real-time progress in your notification shade and lock screen.",
                    footer =
                        if (state.supportsLiveUpdates)
                            "Android controls whether an eligible notification is promoted. Standard progress notifications remain available."
                        else
                            "Promoted Live Updates require Android 16 or newer. This device uses standard progress notifications.",
                ) {
                    ReminderFeatureRow(
                        Icons.Outlined.Movie,
                        "Active Movie Night",
                        "Runtime progress while your movie is playing",
                    )
                    ReminderFeatureRow(
                        Icons.Outlined.Schedule,
                        "Movie Night countdown",
                        "Time remaining before your scheduled screening",
                    )
                }
            }
        else -> Unit
    }
}

@Composable
private fun ReminderDialogBody(
    description: String,
    footer: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
    ) {
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        content()
        Spacer(Modifier.height(ReelsSpacing.xSmall))
        Text(
            footer,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ReminderFeatureRow(icon: ImageVector, title: String, description: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(ReelsDimensions.settingsDialogInfoIconSize),
            tint = MaterialTheme.colorScheme.primary,
        )
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
