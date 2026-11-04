/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.movienight

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.domain.plans.model.MovieNightStatus
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun MovieNightActions(
    plan: MovieNightPlan,
    onMovieClick: (Int) -> Unit,
    onEdit: (MovieNightPlan) -> Unit,
    onStart: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onFinish: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansActionSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.plansActionSpacing),
    ) {
        when (plan.status) {
            MovieNightStatus.Upcoming -> {
                MovieNightActionButton("Edit", { onEdit(plan) }, Modifier.weight(1f))
                MovieNightActionButton("Cancel", { onCancel(plan.id) })
            }
            MovieNightStatus.StartingSoon -> {
                Button(
                    onClick = { onStart(plan.id) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = ReelsDimensions.plansActionMinHeight),
                    shape = RoundedCornerShape(ReelsDimensions.plansActionCornerSize),
                    contentPadding = PaddingValues(
                        horizontal = ReelsDimensions.plansActionHorizontalPadding,
                        vertical = ReelsDimensions.plansActionVerticalPadding,
                    ),
                ) {
                    MovieNightActionLabel("Start now", emphasized = true)
                }
                MovieNightActionButton("Edit", { onEdit(plan) })
                MovieNightActionButton("Cancel", { onCancel(plan.id) })
            }
            MovieNightStatus.Active -> {
                MovieNightActionButton(
                    label = "Mark watched",
                    onClick = { onFinish(plan.id) },
                    modifier = Modifier.weight(1f),
                    emphasized = true,
                )
                MovieNightActionButton("Pause", { onPause(plan.id) })
            }
            MovieNightStatus.Paused -> {
                MovieNightActionButton(
                    label = "Resume",
                    onClick = { onResume(plan.id) },
                    modifier = Modifier.weight(1f),
                    emphasized = true,
                )
                MovieNightActionButton("End night", { onCancel(plan.id) })
            }
            MovieNightStatus.Completed -> {
                MovieNightActionButton("Open movie", { onMovieClick(plan.movieId) }, Modifier.weight(1f))
                MovieNightActionButton("Dismiss", { onRemove(plan.id) })
            }
            MovieNightStatus.Missed -> {
                MovieNightActionButton("Reschedule", { onEdit(plan) }, Modifier.weight(1f))
                MovieNightActionButton("Dismiss", { onRemove(plan.id) })
            }
            MovieNightStatus.Cancelled -> {
                MovieNightActionButton("Dismiss", { onRemove(plan.id) }, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun MovieNightActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    val accent = MaterialTheme.colorScheme.primary
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = ReelsDimensions.plansActionMinHeight),
        shape = RoundedCornerShape(ReelsDimensions.plansActionCornerSize),
        border = BorderStroke(
            1.dp,
            if (emphasized) accent.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (emphasized) accent.copy(alpha = 0.1f) else Color.Transparent,
            contentColor = if (emphasized) accent else MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(
            horizontal = ReelsDimensions.plansActionHorizontalPadding,
            vertical = ReelsDimensions.plansActionVerticalPadding,
        ),
    ) {
        MovieNightActionLabel(label, emphasized)
    }
}

@Composable
private fun MovieNightActionLabel(label: String, emphasized: Boolean) {
    Text(
        text = label,
        fontSize = ReelsDimensions.plansActionTextSize,
        lineHeight = ReelsDimensions.plansActionLineHeight,
        fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Medium,
    )
}
