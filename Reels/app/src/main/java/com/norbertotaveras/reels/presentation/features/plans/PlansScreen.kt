/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.model.plans.MovieNightPlan
import com.norbertotaveras.reels.domain.model.plans.ReleaseReminder
import com.norbertotaveras.reels.domain.model.plans.TrackedRelease
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
fun PlansScreen(
    state: PlansUiState,
    onBackClick: () -> Unit,
    onBrowseReleases: () -> Unit,
    onBrowseMovies: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTabSelected: (PlansTab) -> Unit,
    onReleaseRemove: (Int) -> Unit,
    onReleaseEdit: (TrackedRelease, ReleaseReminder, Long?, Boolean, () -> Unit) -> Unit,
    onClearPlanError: () -> Unit,
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
    var editingRelease by remember { mutableStateOf<TrackedRelease?>(null) }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(
                        start = ReelsDimensions.plansHeaderHorizontalPadding,
                        top = ReelsDimensions.plansHeaderTopPadding,
                        end = ReelsDimensions.plansHeaderHorizontalPadding,
                        bottom = ReelsDimensions.plansHeaderBottomPadding,
                    ),
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.plansHeaderSpacing),
        ) {
            Surface(
                modifier = Modifier.size(ReelsDimensions.plansBackButtonSize),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, reelsBorderColor()),
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate back",
                        modifier = Modifier.size(ReelsDimensions.plansBackIconSize),
                    )
                }
            }
            Text(
                text = "Plans",
                style =
                    MaterialTheme.typography.headlineSmall.copy(
                        fontSize = ReelsDimensions.plansTitleSize,
                        lineHeight = ReelsDimensions.plansTitleLineHeight,
                    ),
                fontWeight = FontWeight.Medium,
            )
        }

        SecondaryTabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.background,
            divider = {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            },
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier =
                        Modifier.tabIndicatorOffset(state.selectedTab.ordinal)
                            .padding(horizontal = ReelsDimensions.plansTabIndicatorHorizontalInset)
                            .height(ReelsDimensions.plansTabIndicatorHeight),
                    color = MaterialTheme.colorScheme.primary,
                )
            },
        ) {
            PlansTab.entries.forEach { tab ->
                Tab(
                    modifier = Modifier.height(ReelsDimensions.plansTabHeight),
                    selected = state.selectedTab == tab,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            tab.label,
                            style =
                                MaterialTheme.typography.labelLarge.copy(
                                    fontSize = ReelsDimensions.plansTabTextSize,
                                    lineHeight = ReelsDimensions.plansTabTextLineHeight,
                                ),
                            fontWeight = FontWeight.Medium,
                        )
                    },
                )
            }
        }

        when (state.selectedTab) {
            PlansTab.Releases ->
                ReleasePlansList(
                    onBrowse = onBrowseReleases,
                    releases = state.trackedReleases,
                    onMovieClick = onMovieClick,
                    onRemove = onReleaseRemove,
                    onEdit = {
                        onClearPlanError()
                        editingRelease = it
                    },
                )
            PlansTab.MovieNights ->
                MovieNightPlansList(
                    onBrowse = onBrowseMovies,
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

    editingRelease?.let { release ->
        LaunchedEffect(release.movieId) { onRefreshExactAlarmAccess() }
        EditReleaseReminderSheet(
            release = release,
            isSaving = state.isSaving,
            canScheduleExactAlarms = state.canScheduleExactAlarms,
            canPostNotifications = state.canPostNotifications,
            errorMessage = state.errorMessage,
            onPermissionResult = onRefreshExactAlarmAccess,
            onSave = { reminder, customAt, notifications ->
                onReleaseEdit(release, reminder, customAt, notifications) { editingRelease = null }
            },
            onDismiss = { editingRelease = null },
        )
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
                onMovieNightEdit(plan, scheduledAt, runtime, reminder, venue, attendees, note) {
                    editingPlan = null
                }
            },
            onDismiss = { editingPlan = null },
        )
    }
}
