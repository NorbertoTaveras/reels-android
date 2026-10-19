/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun PreciseAlarmNotice(
    canScheduleExactAlarms: Boolean,
    onRequestExactAlarms: () -> Unit,
) {
    if (!canScheduleExactAlarms && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ReelsDimensions.planSheetNoticeCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        ) {
            Column(
                modifier = Modifier.padding(ReelsDimensions.planSheetNoticePadding),
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.planSheetHeaderSpacing),
            ) {
                Text(
                    text = "Precise alarm access is off. Reels will use a short inexact delivery window.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onRequestExactAlarms) {
                    Text("Allow precise reminders")
                }
            }
        }
    }
}
