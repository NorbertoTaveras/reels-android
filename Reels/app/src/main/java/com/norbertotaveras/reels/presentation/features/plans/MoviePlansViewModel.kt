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
import com.norbertotaveras.reels.domain.model.plans.MovieNightStatus
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.domain.plans.PlanScheduler
import com.norbertotaveras.reels.domain.plans.ReelsNotificationManager
import com.norbertotaveras.reels.domain.repository.plans.MovieNightRepository
import com.norbertotaveras.reels.domain.repository.plans.TrackedReleaseRepository
import com.norbertotaveras.reels.domain.usecase.plans.ScheduleMovieNightUseCase
import com.norbertotaveras.reels.domain.usecase.plans.TrackReleaseUseCase
import com.norbertotaveras.reels.domain.usecase.plans.UpdateMovieNightUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MoviePlansViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val trackReleaseUseCase: TrackReleaseUseCase,
    private val scheduleMovieNightUseCase: ScheduleMovieNightUseCase,
    private val updateMovieNightUseCase: UpdateMovieNightUseCase,
    trackedReleaseRepository: TrackedReleaseRepository,
    movieNightRepository: MovieNightRepository,
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
            combine(
                trackedReleaseRepository.observeTrackedRelease(movieId),
                movieNightRepository.observeUpcomingMovieNight(movieId),
            ) { release, movieNight -> release to movieNight }
                .collect { (release, movieNight) ->
                    _state.update {
                        it.copy(
                            trackedRelease = release,
                            movieNightPlan = movieNight,
                        )
                    }
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
        customReminderAt: Long?,
        notificationsEnabled: Boolean,
        onComplete: () -> Unit,
    ) {
        val release = state.value.trackedRelease?.copy(
            reminder = reminder,
            customReminderAt = customReminderAt,
            notificationsEnabled = notificationsEnabled,
        ) ?: TrackedRelease(
            movieId = movie.id,
            title = movie.title,
            posterPath = movie.posterPath,
            releaseDate = movie.releaseDate,
            reminder = reminder,
            customReminderAt = customReminderAt,
            notificationsEnabled = notificationsEnabled,
        )
        save(onComplete) {
            trackReleaseUseCase(release)
        }
    }

    fun removeTrackedRelease(onComplete: () -> Unit) {
        save(onComplete) { trackReleaseUseCase.remove(movieId) }
    }

    fun saveMovieNight(
        movie: MovieDetailsDataModel,
        scheduledAt: Long,
        runtimeMinutes: Int,
        reminderMinutes: Int?,
        venue: String?,
        attendeeCount: Int?,
        note: String?,
        onComplete: (MovieNightPlan) -> Unit,
    ) {
        val existingPlan = state.value.movieNightPlan
        val plan = existingPlan?.copy(
            scheduledAt = scheduledAt,
            runtimeMinutes = runtimeMinutes,
            reminderMinutesBefore = reminderMinutes,
            venue = venue?.takeIf(String::isNotBlank),
            attendeeCount = attendeeCount,
            note = note?.takeIf(String::isNotBlank),
            status = MovieNightStatus.Upcoming,
        ) ?: MovieNightPlan(
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

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val result = if (existingPlan == null) {
                scheduleMovieNightUseCase(plan)
            } else {
                updateMovieNightUseCase.edit(plan)
            }
            result.fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false, movieNightPlan = plan) }
                    onComplete(plan)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Unable to save this Movie Night.",
                        )
                    }
                },
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
