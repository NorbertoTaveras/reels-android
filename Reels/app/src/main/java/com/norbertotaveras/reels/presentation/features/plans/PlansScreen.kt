/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
fun PlansScreen(
    state: PlansUiState,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTabSelected: (PlansTab) -> Unit,
    onReleaseRemove: (Int) -> Unit,
    onMovieNightEdit: (MovieNightPlan, Long, Int, Int?, String?, Int?, String?, () -> Unit) -> Unit,
    onMovieNightStart: (String) -> Unit,
    onMovieNightPause: (String) -> Unit,
    onMovieNightResume: (String) -> Unit,
    onMovieNightFinish: (String) -> Unit,
    onMovieNightCancel: (String) -> Unit,
    onMovieNightRemove: (String) -> Unit,
    onRefreshExactAlarmAccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingPlan by remember { mutableStateOf<MovieNightPlan?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ReelsSpacing.screenHorizontal, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, reelsBorderColor()),
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate back")
                }
            }
            Text("Plans", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        }

        SecondaryTabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.background,
            divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(state.selectedTab.ordinal),
                    color = MaterialTheme.colorScheme.primary,
                )
            },
        ) {
            PlansTab.entries.forEach { tab ->
                Tab(
                    selected = state.selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = { Text(tab.label) },
                )
            }
        }

        when (state.selectedTab) {
            PlansTab.Releases -> ReleasePlansList(
                releases = state.trackedReleases,
                onMovieClick = onMovieClick,
                onRemove = onReleaseRemove,
            )
            PlansTab.MovieNights -> MovieNightPlansList(
                plans = state.movieNights,
                onMovieClick = onMovieClick,
                onEdit = { editingPlan = it },
                onStart = onMovieNightStart,
                onPause = onMovieNightPause,
                onResume = onMovieNightResume,
                onFinish = onMovieNightFinish,
                onCancel = onMovieNightCancel,
                onRemove = onMovieNightRemove,
            )
        }
    }

    editingPlan?.let { plan ->
        LaunchedEffect(Unit) { onRefreshExactAlarmAccess() }
        EditMovieNightSheet(
            plan = plan,
            isSaving = state.isSaving,
            canScheduleExactAlarms = state.canScheduleExactAlarms,
            canPostNotifications = state.canPostNotifications,
            errorMessage = state.errorMessage,
            onSave = { scheduledAt, runtime, reminder, venue, attendees, note ->
                onMovieNightEdit(
                    plan,
                    scheduledAt,
                    runtime,
                    reminder,
                    venue,
                    attendees,
                    note,
                ) { editingPlan = null }
            },
            onDismiss = { editingPlan = null },
        )
    }
}
