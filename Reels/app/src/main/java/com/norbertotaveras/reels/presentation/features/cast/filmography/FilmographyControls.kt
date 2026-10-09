/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.model.person.FilmographySort
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun FilmographySortRow(
    selectedSort: FilmographySort,
    onSortClick: (FilmographySort) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.filmographyFilterSpacing),
        contentPadding = PaddingValues(end = ReelsSpacing.screenHorizontal)
    ) {
        items(
            count = FilmographySort.entries.size,
            key = { index -> FilmographySort.entries[index].name }
        ) { index ->
            val sort = FilmographySort.entries[index]

            FilmographyFilterChip(
                label = sort.displayLabel,
                selected = selectedSort == sort,
                onClick = {
                    onSortClick(sort)
                }
            )
        }
    }
}

@Composable
private fun FilmographyFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (selected) {
            ReelsAccent.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (selected) {
            ReelsAccent
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                ReelsAccent
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.filmographyFilterHorizontalPadding,
                vertical = ReelsDimensions.filmographyFilterVerticalPadding
            ),
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp
            ),
            fontWeight = FontWeight.Medium
        )
    }
}
