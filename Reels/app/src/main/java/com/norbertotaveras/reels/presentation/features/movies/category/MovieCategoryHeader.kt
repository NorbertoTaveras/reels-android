/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun MovieCategoryHeader(
    category: MovieCategory,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    movieCount: Int? = null,
    hasMoreMovies: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = ReelsDimensions.categoryHeaderBottomPadding),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryHeaderContentSpacing)
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.categoryBackButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            )
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(ReelsDimensions.categoryBackIconSize)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryHeaderTextSpacing)
        ) {
            Text(
                text = category.label,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    lineHeight = 30.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = category.supportingText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            movieCount?.takeIf { count -> count > 0 }?.let { count ->
                MovieCategoryCountChip(
                    modifier = Modifier.padding(top = ReelsDimensions.categoryHeaderCountSpacing),
                    movieCount = count,
                    hasMoreMovies = hasMoreMovies
                )
            }
        }
    }
}

@Composable
private fun MovieCategoryCountChip(
    movieCount: Int,
    hasMoreMovies: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.categoryCountHorizontalPadding,
                vertical = ReelsDimensions.categoryCountVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Movie,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.categoryCountIconSize)
            )

            Text(
                text = if (hasMoreMovies) "$movieCount+ movies" else "$movieCount movies",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            )
        }
    }
}
