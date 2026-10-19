/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditMovieNightSheet(
    plan: MovieNightPlan,
    isSaving: Boolean,
    canScheduleExactAlarms: Boolean,
    canPostNotifications: Boolean,
    errorMessage: String?,
    onSave: (Long, Int, Int?, String?, Int?, String?) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            MovieNightPlanForm(
                movieTitle = plan.title,
                posterPath = plan.posterPath,
                initialRuntimeMinutes = plan.runtimeMinutes,
                isSaving = isSaving,
                canScheduleExactAlarms = canScheduleExactAlarms,
                canPostNotifications = canPostNotifications,
                onDismiss = onDismiss,
                onSave = onSave,
                existingPlan = plan,
            )
            errorMessage?.let { message ->
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
