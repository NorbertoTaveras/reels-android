/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.domain.model.settings.PlanPreferences

data class MoviePlansUiState(
    val preferences: PlanPreferences = PlanPreferences(),
    val trackedRelease: TrackedRelease? = null,
    val movieNightPlan: MovieNightPlan? = null,
    val isSaving: Boolean = false,
    val canScheduleExactAlarms: Boolean = true,
    val canPostNotifications: Boolean = true,
    val errorMessage: String? = null,
)
