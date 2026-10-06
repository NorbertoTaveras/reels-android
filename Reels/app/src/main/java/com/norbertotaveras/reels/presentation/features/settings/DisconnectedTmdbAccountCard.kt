/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbBlue
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbOnBlue
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
internal fun DisconnectedTmdbAccountCard(
    isLoading: Boolean,
    onConnectClick: () -> Unit,
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TmdbConnectionLogoMark()

            Spacer(modifier = Modifier.width(ReelsSpacing.large))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Connect to TMDB",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = ReelsDimensions.settingsTmdbConnectedTitleSize,
                        lineHeight = ReelsDimensions.settingsTmdbConnectedTitleLineHeight,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Sync watchlist and ratings",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = ReelsDimensions.settingsTmdbConnectedBodySize,
                        lineHeight = ReelsDimensions.settingsTmdbConnectedBodyLineHeight,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(ReelsDimensions.settingsTmdbButtonHeight),
            enabled = !isLoading,
            onClick = onConnectClick,
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
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.settingsTmdbButtonIconSize)
                )

                Spacer(modifier = Modifier.width(ReelsSpacing.small))

                Text(text = "Sign in with TMDB")
            }
        }
    }
}
