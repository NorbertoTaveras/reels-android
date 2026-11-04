/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.settings.model

import com.norbertotaveras.reels.domain.plans.model.ReleaseReminder

data class PlanPreferences(
    val releaseReminder: ReleaseReminder = ReleaseReminder.OneDayBefore,
    val movieNightReminderMinutes: Int? = 30,
    val liveUpdatesEnabled: Boolean = true,
)
