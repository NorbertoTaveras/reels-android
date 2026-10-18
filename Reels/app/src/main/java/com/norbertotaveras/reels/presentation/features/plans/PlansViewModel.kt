/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.usecase.plans.ObservePlansUseCase
import com.norbertotaveras.reels.domain.usecase.plans.TrackReleaseUseCase
import com.norbertotaveras.reels.domain.usecase.plans.UpdateMovieNightUseCase
import com.norbertotaveras.reels.domain.repository.plans.MovieNightRepository
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PlansViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observePlansUseCase: ObservePlansUseCase,
    private val trackReleaseUseCase: TrackReleaseUseCase,
    private val updateMovieNightUseCase: UpdateMovieNightUseCase,
    private val notificationManager: ReelsNotificationManager,
    private val movieNightRepository: MovieNightRepository,
    private val scheduler: PlanScheduler,
) : ViewModel() {
    private val _state = MutableStateFlow(
        PlansUiState(
            selectedTab = PlansTab.fromRoute(savedStateHandle[ReelsDestinations.PLANS_TAB_ARG]),
            canScheduleExactAlarms = scheduler.canScheduleExactAlarms(),
            canPostNotifications = notificationManager.canPostNotifications(),
        )
    )
    val state: StateFlow<PlansUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                observePlansUseCase.trackedReleases,
                observePlansUseCase.movieNights,
            ) { releases, movieNights -> releases to movieNights }
                .collect { (releases, movieNights) ->
                    _state.update {
                        it.copy(
                            trackedReleases = releases.sortedBy { release -> release.releaseDate ?: MAX_DATE },
                            movieNights = movieNights.sortedBy { plan -> plan.scheduledAt },
                        )
                    }
                }
        }
    }

    fun selectTab(tab: PlansTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    fun removeRelease(movieId: Int) {
        viewModelScope.launch { trackReleaseUseCase.remove(movieId) }
    }

    fun startMovieNight(id: String) = updateMovieNight {
        if (updateMovieNightUseCase.start(id).isSuccess) {
            currentMovieNight(id)?.let(notificationManager::showMovieNightProgress)
        }
    }

    fun pauseMovieNight(id: String) = updateMovieNight {
        if (updateMovieNightUseCase.pause(id).isSuccess) {
            currentMovieNight(id)?.let(notificationManager::showMovieNightProgress)
        }
    }

    fun resumeMovieNight(id: String) = updateMovieNight {
        if (updateMovieNightUseCase.resume(id).isSuccess) {
            currentMovieNight(id)?.let(notificationManager::showMovieNightProgress)
        }
    }

    fun finishMovieNight(id: String) = updateMovieNight {
        if (updateMovieNightUseCase.finish(id).isSuccess) {
            currentMovieNight(id)?.let(notificationManager::showMovieNightCompleted)
        }
    }

    fun cancelMovieNight(id: String) = updateMovieNight {
        if (updateMovieNightUseCase.cancel(id).isSuccess) {
            notificationManager.cancelMovieNightProgress(id)
        }
    }

    fun removeMovieNight(id: String) = updateMovieNight {
        updateMovieNightUseCase.remove(id)
        notificationManager.cancelMovieNightProgress(id)
    }

    fun editMovieNight(
        original: MovieNightPlan,
        scheduledAt: Long,
        runtimeMinutes: Int,
        reminderMinutes: Int?,
        venue: String?,
        attendeeCount: Int?,
        note: String?,
        onComplete: () -> Unit,
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            updateMovieNightUseCase.edit(
                original.copy(
                    scheduledAt = scheduledAt,
                    runtimeMinutes = runtimeMinutes,
                    reminderMinutesBefore = reminderMinutes,
                    venue = venue?.takeIf(String::isNotBlank),
                    attendeeCount = attendeeCount,
                    note = note?.takeIf(String::isNotBlank),
                )
            ).fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false) }
                    onComplete()
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Unable to update this Movie Night.",
                        )
                    }
                },
            )
        }
    }

    fun refreshExactAlarmAccess() {
        _state.update {
            it.copy(
                canScheduleExactAlarms = scheduler.canScheduleExactAlarms(),
                canPostNotifications = notificationManager.canPostNotifications(),
            )
        }
    }

    private fun updateMovieNight(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    private suspend fun currentMovieNight(id: String) =
        movieNightRepository.getMovieNights().firstOrNull { it.id == id }

    private companion object {
        const val MAX_DATE = "9999-12-31"
    }
}
