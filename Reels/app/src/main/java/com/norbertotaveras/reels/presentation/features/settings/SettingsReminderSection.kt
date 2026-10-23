/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.norbertotaveras.reels.presentation.ui.extensions.defaultReminderLabel
import com.norbertotaveras.reels.presentation.ui.extensions.movieNightReminderLabel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal

@Composable
internal fun SettingsReminderSection(state: SettingsUiState, onEvent: (SettingsEvent) -> Unit) {
    SettingsSection(title = "NOTIFICATIONS & REMINDERS") {
        SettingsRow(
            icon = Icons.Outlined.NotificationsNone,
            title = "Notifications",
            subtitle = if (state.canPostNotifications) "Allowed" else "Not allowed",
            subtitleColor =
                if (state.canPostNotifications) ReelsTeal
                else MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = {
                onEvent(SettingsEvent.PlanPreferenceClicked(SettingsPreferenceDialog.Notifications))
            },
        )
        SettingsDivider()
        SettingsRow(
            icon = Icons.Outlined.Bolt,
            title = "Live Updates",
            subtitle =
                when {
                    !state.supportsLiveUpdates -> "Requires Android 16+"
                    !state.planPreferences.liveUpdatesEnabled -> "Disabled"
                    !state.canPostNotifications || !state.canPromoteLiveUpdates -> "Not allowed"
                    else -> "Enabled"
                },
            subtitleColor =
                if (
                    state.supportsLiveUpdates &&
                        state.canPostNotifications &&
                        state.canPromoteLiveUpdates &&
                        state.planPreferences.liveUpdatesEnabled
                )
                    ReelsTeal
                else MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = {
                onEvent(SettingsEvent.PlanPreferenceClicked(SettingsPreferenceDialog.LiveUpdates))
            },
        )
        SettingsDivider()
        SettingsRow(
            icon = Icons.Outlined.Alarm,
            title = "Precise reminders",
            subtitle = if (state.canScheduleExactAlarms) "Allowed" else "Not allowed",
            subtitleColor =
                if (state.canScheduleExactAlarms) ReelsTeal
                else MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = {
                onEvent(
                    SettingsEvent.PlanPreferenceClicked(SettingsPreferenceDialog.PreciseReminders)
                )
            },
        )
        SettingsDivider()
        SettingsRow(
            icon = Icons.Outlined.CalendarMonth,
            title = "Release reminder",
            subtitle = "${state.planPreferences.releaseReminder.defaultReminderLabel()} · 9:00 AM",
            onClick = {
                onEvent(
                    SettingsEvent.PlanPreferenceClicked(SettingsPreferenceDialog.ReleaseReminder)
                )
            },
        )
        SettingsDivider()
        SettingsRow(
            icon = Icons.Outlined.Schedule,
            title = "Movie Night reminder",
            subtitle = state.planPreferences.movieNightReminderMinutes.movieNightReminderLabel(),
            onClick = {
                onEvent(
                    SettingsEvent.PlanPreferenceClicked(SettingsPreferenceDialog.MovieNightReminder)
                )
            },
        )
        SettingsDivider()
        SettingsRow(
            icon = Icons.Outlined.CalendarMonth,
            title = "Manage plans",
            subtitle = "Tracked releases and movie nights",
            onClick = { onEvent(SettingsEvent.PlansClicked) },
        )
    }
}
