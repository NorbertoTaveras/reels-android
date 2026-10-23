/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.usecase.settings

import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.settings.PlanPreferences
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.repository.plans.MovieNightRepository
import com.norbertotaveras.reels.domain.repository.settings.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class UpdatePlanPreferencesUseCase
@Inject
constructor(
    private val repository: SettingsRepository,
    private val movieNightRepository: MovieNightRepository,
    private val notificationManager: ReelsNotificationManager,
) {
    suspend operator fun invoke(preferences: PlanPreferences) {
        require(
            preferences.releaseReminder in
                listOf(
                    ReleaseReminder.OnReleaseDay,
                    ReleaseReminder.OneDayBefore,
                    ReleaseReminder.OneWeekBefore,
                )
        )
        require(
            preferences.movieNightReminderMinutes == null ||
                preferences.movieNightReminderMinutes in listOf(15, 30, 60, 120)
        )
        val previous = repository.observeSettings().first().planPreferences
        repository.updatePlanPreferences(preferences)
        if (previous.liveUpdatesEnabled != preferences.liveUpdatesEnabled) {
            movieNightRepository.getMovieNights().forEach { plan ->
                when (plan.status) {
                    MovieNightStatus.StartingSoon ->
                        notificationManager.showMovieNightCountdown(plan)
                    MovieNightStatus.Active,
                    MovieNightStatus.Paused -> notificationManager.showMovieNightProgress(plan)
                    else -> Unit
                }
            }
        }
    }
}
