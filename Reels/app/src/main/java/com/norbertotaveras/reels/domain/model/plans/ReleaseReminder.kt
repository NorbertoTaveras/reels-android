/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.model.plans

enum class ReleaseReminder(val minutesBefore: Int?) {
    AppDefault(1_440),
    OnReleaseDay(0),
    OneDayBefore(1_440),
    OneWeekBefore(10_080),
    Custom(null),
    None(null),
}
