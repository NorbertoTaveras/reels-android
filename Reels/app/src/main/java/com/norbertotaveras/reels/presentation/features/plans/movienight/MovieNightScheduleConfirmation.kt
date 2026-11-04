/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.movienight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.norbertotaveras.reels.domain.plans.model.MovieNightPlan
import com.norbertotaveras.reels.presentation.features.plans.shared.reminderLabel
import com.norbertotaveras.reels.presentation.features.plans.shared.scheduleConfirmationDateTime
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal

@Composable
internal fun MovieNightScheduleConfirmation(plan: MovieNightPlan) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = ReelsDimensions.planSheetHorizontalPadding,
                end = ReelsDimensions.planSheetHorizontalPadding,
                top = ReelsDimensions.movieNightConfirmationTopPadding,
                bottom = ReelsDimensions.movieNightConfirmationBottomPadding,
            )
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(ReelsDimensions.movieNightConfirmationIconContainerSize)
                .background(
                    ReelsTeal.copy(alpha = 0.15f),
                    RoundedCornerShape(ReelsDimensions.movieNightConfirmationIconCornerSize),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = ReelsTeal,
                modifier = Modifier.size(ReelsDimensions.movieNightConfirmationIconSize),
            )
        }
        Spacer(Modifier.height(ReelsDimensions.movieNightConfirmationIconSpacing))
        Text(
            text = "Movie night scheduled!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(ReelsDimensions.movieNightConfirmationTextSpacing))
        Text(
            text = plan.scheduleConfirmationDateTime(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(ReelsDimensions.movieNightConfirmationTextSpacing))
        Text(
            text = if (plan.reminderMinutesBefore == null) plan.reminderLabel()
            else "Reminder ${plan.reminderLabel()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
