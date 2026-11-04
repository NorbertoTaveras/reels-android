/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.extensions.toDisplayAge
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun PersonalInfoSection(
    birthday: String?,
    knownCreditsCount: Int,
    alsoKnownAs: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Text(
            text = "Personal Info",
            style = castDetailsSectionTitleStyle(),
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ReelsDimensions.castDetailsCardCornerRadius),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
        ) {
            Column {
                PersonalInfoRow(
                    label = "Age",
                    value = birthday.toDisplayAge() ?: "Unknown"
                )

                PersonalInfoDivider()

                PersonalInfoRow(
                    label = "Known Credits",
                    value = knownCreditsCount.toString()
                )

                if (!alsoKnownAs.isNullOrBlank()) {
                    PersonalInfoDivider()

                    PersonalInfoRow(
                        label = "Also Known As",
                        value = alsoKnownAs
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsSpacing.large,
                vertical = ReelsSpacing.large
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun PersonalInfoDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline
    )
}
