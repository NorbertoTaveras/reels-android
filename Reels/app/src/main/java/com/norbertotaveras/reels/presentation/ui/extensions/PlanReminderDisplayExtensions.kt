/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder

internal fun ReleaseReminder.defaultReminderLabel(): String =
    when (this) {
        ReleaseReminder.OnReleaseDay -> "On release day"
        ReleaseReminder.OneWeekBefore -> "1 week before"
        else -> "1 day before"
    }

internal fun Int?.movieNightReminderLabel(): String =
    when (this) {
        null -> "No reminder"
        60 -> "1 hour before"
        120 -> "2 hours before"
        else -> "$this minutes before"
    }
