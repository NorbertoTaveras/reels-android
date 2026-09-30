/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DiscoverFilterSheetFooter(
    movieCount: Int?,
    isLoading: Boolean,
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f),
                thickness = 1.dp
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ReelsSpacing.screenHorizontal,
                        vertical = ReelsSpacing.large
                    )
                    .height(48.dp),
                onClick = onApplyClick,
                enabled = !isLoading,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(ReelsSpacing.small))
                }

                Text(
                    text = if (isLoading) {
                        "Checking movies…"
                    } else {
                        movieCount?.let { count ->
                            "Show ${count.toMovieCountLabel(capitalize = true)}"
                        } ?: "Show Movies"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
