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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditReleaseReminderSheet(
    release: TrackedRelease,
    isSaving: Boolean,
    canScheduleExactAlarms: Boolean,
    canPostNotifications: Boolean,
    errorMessage: String?,
    onPermissionResult: () -> Unit,
    onSave: (ReleaseReminder, Long?, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            onPermissionResult()
        }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(ReelsDimensions.planSheetCornerSize),
        dragHandle = { MovieNightSheetDragHandle() },
    ) {
        ReleasePlanForm(
            movieTitle = release.title,
            posterPath = release.posterPath,
            initialReleaseDate = release.releaseDate,
            trackedRelease = release,
            isSaving = isSaving,
            canScheduleExactAlarms = canScheduleExactAlarms,
            canPostNotifications = canPostNotifications,
            errorMessage = errorMessage,
            onDismiss = onDismiss,
            onSave = { reminder, customReminderAt, notifications ->
                if (
                    notifications &&
                        reminder != ReleaseReminder.None &&
                        !canPostNotifications &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                onSave(reminder, customReminderAt, notifications)
            },
        )
    }
}
