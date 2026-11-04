/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.sheet

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.norbertotaveras.reels.domain.movie.model.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.ReleaseReminder
import com.norbertotaveras.reels.presentation.features.plans.movienight.MovieNightPlanForm
import com.norbertotaveras.reels.presentation.features.plans.movienight.MovieNightScheduleConfirmation
import com.norbertotaveras.reels.presentation.features.plans.movienight.MovieNightSheetDragHandle
import com.norbertotaveras.reels.presentation.features.plans.release.ReleasePlanForm
import com.norbertotaveras.reels.presentation.features.plans.shared.PlanFeedbackTiming
import com.norbertotaveras.reels.presentation.features.plans.shared.releaseReminderOptions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviePlansSheet(
    movie: MovieDetailsDataModel,
    viewModel: MoviePlansViewModel,
    onDismiss: () -> Unit,
    onMovieNightSaved: (MovieNightPlan) -> Unit,
    onReleaseFeedback: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var mode by rememberSaveable { mutableStateOf(MoviePlansSheetMode.Choice) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val onSaved by rememberUpdatedState(onMovieNightSaved)
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewModel.refreshExactAlarmAccess()
        }

    LaunchedEffect(Unit) { viewModel.refreshExactAlarmAccess() }
    LaunchedEffect(mode, state.movieNightPlan?.id) {
        if (mode == MoviePlansSheetMode.MovieNightConfirmation) {
            val plan = state.movieNightPlan ?: return@LaunchedEffect
            delay(PlanFeedbackTiming.MOVIE_NIGHT_CONFIRMATION_MILLIS.milliseconds)
            sheetState.hide()
            onSaved(plan)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(ReelsDimensions.planSheetCornerSize),
        dragHandle = { MovieNightSheetDragHandle() },
    ) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .then(
                        if (
                            mode == MoviePlansSheetMode.MovieNight ||
                                mode == MoviePlansSheetMode.Release
                        )
                            Modifier
                        else Modifier.verticalScroll(rememberScrollState())
                    )
        ) {
            when (mode) {
                MoviePlansSheetMode.Choice ->
                    PlanTypeChoice(
                        movieTitle = movie.title,
                        releaseDate = state.trackedRelease?.releaseDate ?: movie.releaseDate,
                        isTracked = state.trackedRelease != null,
                        movieNightPlan = state.movieNightPlan,
                        showRelease = state.trackedRelease != null || movie.isUnreleased(),
                        onReleaseClick = { mode = MoviePlansSheetMode.Release },
                        onMovieNightClick = { mode = MoviePlansSheetMode.MovieNight },
                        onDismiss = onDismiss,
                        onStopTracking = {
                            viewModel.removeTrackedRelease {
                                onReleaseFeedback("Release tracking stopped")
                            }
                        },
                        isSaving = state.isSaving,
                    )
                MoviePlansSheetMode.Release ->
                    ReleasePlanForm(
                        defaultReminder = state.preferences.releaseReminder,
                        movieTitle = movie.title,
                        posterPath = movie.posterPath,
                        initialReleaseDate = movie.releaseDate,
                        trackedRelease = state.trackedRelease,
                        isSaving = state.isSaving,
                        canScheduleExactAlarms = state.canScheduleExactAlarms,
                        canPostNotifications = state.canPostNotifications,
                        onDismiss = onDismiss,
                        onSave = { reminder, customReminderAt, notifications ->
                            if (
                                notifications &&
                                    reminder != ReleaseReminder.None &&
                                    !state.canPostNotifications &&
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            ) {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                            viewModel.trackRelease(
                                movie = movie,
                                reminder = reminder,
                                customReminderAt = customReminderAt,
                                notificationsEnabled = notifications,
                                onComplete = {
                                    val label =
                                        releaseReminderOptions.first { it.value == reminder }.title
                                    onReleaseFeedback("Release tracked · $label")
                                },
                            )
                        },
                        errorMessage = state.errorMessage,
                    )
                MoviePlansSheetMode.MovieNight ->
                    MovieNightPlanForm(
                        defaultReminderMinutes = state.preferences.movieNightReminderMinutes,
                        movieTitle = movie.title,
                        posterPath = movie.posterPath,
                        initialRuntimeMinutes = movie.runtime,
                        isSaving = state.isSaving,
                        canScheduleExactAlarms = state.canScheduleExactAlarms,
                        canPostNotifications = state.canPostNotifications,
                        existingPlan = state.movieNightPlan,
                        errorMessage = state.errorMessage,
                        onDismiss = onDismiss,
                        onSave = { scheduledAt, runtime, reminder, venue, attendees, note ->
                            if (
                                reminder != null &&
                                    !state.canPostNotifications &&
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            ) {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }
                            viewModel.saveMovieNight(
                                movie = movie,
                                scheduledAt = scheduledAt,
                                runtimeMinutes = runtime,
                                reminderMinutes = reminder,
                                venue = venue,
                                attendeeCount = attendees,
                                note = note,
                                onComplete = { mode = MoviePlansSheetMode.MovieNightConfirmation },
                            )
                        },
                    )
                MoviePlansSheetMode.MovieNightConfirmation ->
                    state.movieNightPlan?.let { plan ->
                        MovieNightScheduleConfirmation(plan = plan)
                    }
            }
            state.errorMessage
                ?.takeIf { mode == MoviePlansSheetMode.Choice }
                ?.let { message ->
                    Text(
                        text = message,
                        modifier =
                            Modifier.padding(
                                horizontal = ReelsDimensions.planSheetHorizontalPadding,
                                vertical = ReelsDimensions.planSheetSectionVerticalPadding,
                            ),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
        }
    }
}

private fun MovieDetailsDataModel.isUnreleased(): Boolean {
    val date =
        releaseDate?.let { value -> runCatching { java.time.LocalDate.parse(value) }.getOrNull() }
    return date == null || !date.isBefore(java.time.LocalDate.now())
}
