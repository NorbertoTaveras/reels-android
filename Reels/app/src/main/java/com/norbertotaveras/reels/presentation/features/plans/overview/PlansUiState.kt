/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.overview

import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.TrackedRelease

data class PlansUiState(
    val selectedTab: PlansTab = PlansTab.Releases,
    val trackedReleases: List<TrackedRelease> = emptyList(),
    val movieNights: List<MovieNightPlan> = emptyList(),
    val canScheduleExactAlarms: Boolean = true,
    val canPostNotifications: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)
