/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbBlue
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbOnBlue
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
internal fun PendingTmdbAccountCard(
    isLoading: Boolean,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(ReelsDimensions.settingsTmdbCardCornerSize)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = ReelsDimensions.settingsDividerThickness,
                color = reelsBorderColor(),
                shape = cardShape
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = cardShape
            )
            .padding(ReelsDimensions.settingsTmdbCardPadding),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        TmdbAccountHeaderRow(
            title = "Approval pending",
            subtitle = "Approve Reels on themoviedb.org, then finish here.",
            subtitleMaxLines = 2
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(ReelsDimensions.settingsTmdbButtonHeight),
            enabled = !isLoading,
            onClick = onFinishClick,
            shape = RoundedCornerShape(ReelsDimensions.settingsTmdbButtonCornerSize),
            colors = ButtonDefaults.buttonColors(
                containerColor = ReelsTmdbBlue,
                contentColor = ReelsTmdbOnBlue
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = ReelsTmdbOnBlue
                )
            } else {
                Text(text = "Finish Connection")
            }
        }
    }
}
