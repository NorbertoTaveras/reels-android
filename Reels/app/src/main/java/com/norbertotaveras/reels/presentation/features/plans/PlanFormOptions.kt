/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import java.time.format.DateTimeFormatter

internal data class ReleaseReminderOption(
    val value: ReleaseReminder,
    val title: String,
    val subtitle: String? = null,
)

internal val releaseReminderOptions = listOf(
    ReleaseReminderOption(ReleaseReminder.AppDefault, "Use default", "1 day before at 9:00 AM (app setting)"),
    ReleaseReminderOption(ReleaseReminder.OnReleaseDay, "On release day"),
    ReleaseReminderOption(ReleaseReminder.OneDayBefore, "1 day before"),
    ReleaseReminderOption(ReleaseReminder.OneWeekBefore, "1 week before"),
    ReleaseReminderOption(ReleaseReminder.Custom, "Custom date & time"),
    ReleaseReminderOption(ReleaseReminder.None, "No reminder"),
)

internal val movieNightReminderOptions = listOf(
    15 to "15 min",
    30 to "30 min",
    60 to "1 hr",
    120 to "2 hr",
)

internal val planInputDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
internal val planInputTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
internal val planReleaseDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
