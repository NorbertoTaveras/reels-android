/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.plans.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun PlanDateTimeField(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    labelSpacing: Dp = ReelsDimensions.movieNightSheetFieldLabelSpacing,
    trailingIcon: ImageVector? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(labelSpacing)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            modifier =
                Modifier.fillMaxWidth()
                    .height(ReelsDimensions.movieNightSheetFieldHeight)
                    .clickable(onClick = onClick),
            shape = RoundedCornerShape(ReelsDimensions.movieNightSheetFieldCornerSize),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Row(
                modifier =
                    Modifier.padding(horizontal = ReelsDimensions.planSheetFieldHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.plansRowSpacing),
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    value,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                )
                trailingIcon?.let { pickerIcon ->
                    Icon(
                        pickerIcon,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.planSheetCloseIconSize),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
