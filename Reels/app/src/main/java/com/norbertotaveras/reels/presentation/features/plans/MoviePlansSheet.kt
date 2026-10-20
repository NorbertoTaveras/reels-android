/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviePlansSheet(
    movie: MovieDetailsDataModel,
    viewModel: MoviePlansViewModel,
    onDismiss: () -> Unit,
    onMovieNightSaved: (MovieNightPlan) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var mode by rememberSaveable { mutableStateOf(MoviePlansSheetMode.Choice) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val onSaved by rememberUpdatedState(onMovieNightSaved)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { viewModel.refreshExactAlarmAccess() }

    LaunchedEffect(Unit) { viewModel.refreshExactAlarmAccess() }
    LaunchedEffect(mode, state.movieNightPlan?.id) {
        if (mode == MoviePlansSheetMode.MovieNightConfirmation) {
            val plan = state.movieNightPlan ?: return@LaunchedEffect
            delay(PlanFeedbackTiming.movieNightConfirmationMillis)
            sheetState.hide()
            onSaved(plan)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(ReelsDimensions.planSheetCornerSize),
        dragHandle = {
            if (mode == MoviePlansSheetMode.Choice ||
                mode == MoviePlansSheetMode.MovieNight ||
                mode == MoviePlansSheetMode.MovieNightConfirmation
            ) {
                MovieNightSheetDragHandle()
            } else {
                BottomSheetDefaults.DragHandle()
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (mode == MoviePlansSheetMode.MovieNight) Modifier
                    else Modifier.verticalScroll(rememberScrollState())
                ),
        ) {
            when (mode) {
                MoviePlansSheetMode.Choice -> PlanTypeChoice(
                    movieTitle = movie.title,
                    isTracked = state.trackedRelease != null,
                    movieNightPlan = state.movieNightPlan,
                    showRelease = state.trackedRelease != null || movie.isUnreleased(),
                    onReleaseClick = { mode = MoviePlansSheetMode.Release },
                    onMovieNightClick = { mode = MoviePlansSheetMode.MovieNight },
                    onDismiss = onDismiss,
                )
                MoviePlansSheetMode.Release -> ReleasePlanForm(
                    movie = movie,
                    trackedRelease = state.trackedRelease,
                    isSaving = state.isSaving,
                    canScheduleExactAlarms = state.canScheduleExactAlarms,
                    canPostNotifications = state.canPostNotifications,
                    onRequestExactAlarms = context::openExactAlarmSettings,
                    onDismiss = onDismiss,
                    onSave = { reminder, customReminderAt, notifications ->
                        if (notifications && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        viewModel.trackRelease(
                            movie = movie,
                            reminder = reminder,
                            customReminderAt = customReminderAt,
                            notificationsEnabled = notifications,
                            onComplete = onDismiss,
                        )
                    },
                    onRemove = { viewModel.removeTrackedRelease(onDismiss) },
                )
                MoviePlansSheetMode.MovieNight -> MovieNightPlanForm(
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
                        if (reminder != null && !state.canPostNotifications &&
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
                MoviePlansSheetMode.MovieNightConfirmation -> state.movieNightPlan?.let { plan ->
                    MovieNightScheduleConfirmation(plan = plan)
                }
            }
            state.errorMessage?.takeUnless { mode == MoviePlansSheetMode.MovieNight }?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(
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
    val date = releaseDate?.let { value -> runCatching { java.time.LocalDate.parse(value) }.getOrNull() }
    return date == null || !date.isBefore(java.time.LocalDate.now())
}
