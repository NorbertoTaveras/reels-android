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
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.repository.plans.TrackedReleaseRepository
import com.norbertotaveras.reels.domain.usecase.plans.ScheduleMovieNightUseCase
import com.norbertotaveras.reels.domain.usecase.plans.TrackReleaseUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MoviePlansViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val trackReleaseUseCase: TrackReleaseUseCase,
    private val scheduleMovieNightUseCase: ScheduleMovieNightUseCase,
    trackedReleaseRepository: TrackedReleaseRepository,
    private val scheduler: PlanScheduler,
    private val notificationManager: ReelsNotificationManager,
) : ViewModel() {
    private val movieId = checkNotNull(savedStateHandle.get<Int>(ReelsDestinations.MOVIE_ID_ARG))
    private val _state = MutableStateFlow(
        MoviePlansUiState(
            canScheduleExactAlarms = scheduler.canScheduleExactAlarms(),
            canPostNotifications = notificationManager.canPostNotifications(),
        )
    )
    val state: StateFlow<MoviePlansUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            trackedReleaseRepository.observeTrackedRelease(movieId).collect { release ->
                _state.update { it.copy(trackedRelease = release) }
            }
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

    fun trackRelease(
        movie: MovieDetailsDataModel,
        reminder: ReleaseReminder,
        notificationsEnabled: Boolean,
        onComplete: () -> Unit,
    ) {
        save(onComplete) {
            trackReleaseUseCase(
                TrackedRelease(
                    movieId = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    releaseDate = movie.releaseDate,
                    reminder = reminder,
                    notificationsEnabled = notificationsEnabled,
                )
            )
        }
    }

    fun removeTrackedRelease(onComplete: () -> Unit) {
        save(onComplete) { trackReleaseUseCase.remove(movieId) }
    }

    fun scheduleMovieNight(
        movie: MovieDetailsDataModel,
        scheduledAt: Long,
        runtimeMinutes: Int,
        reminderMinutes: Int?,
        venue: String?,
        attendeeCount: Int?,
        note: String?,
        onComplete: () -> Unit,
    ) {
        save(onComplete) {
            scheduleMovieNightUseCase(
                MovieNightPlan(
                    id = UUID.randomUUID().toString(),
                    movieId = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    scheduledAt = scheduledAt,
                    runtimeMinutes = runtimeMinutes,
                    reminderMinutesBefore = reminderMinutes,
                    venue = venue?.takeIf(String::isNotBlank),
                    attendeeCount = attendeeCount,
                    note = note?.takeIf(String::isNotBlank),
                )
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun save(onComplete: () -> Unit, operation: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            operation().fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false) }
                    onComplete()
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(isSaving = false, errorMessage = error.message ?: "Unable to save this plan.")
                    }
                }
            )
        }
    }
}
