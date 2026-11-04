/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.domain.movie.model.MovieCategory
import com.norbertotaveras.reels.presentation.ui.extensions.displayLabel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions

@Composable
internal fun MovieCategoryLoadingState(
    category: MovieCategory,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            start = ReelsDimensions.categoryScreenHorizontalPadding,
            top = ReelsDimensions.categoryScreenTopPadding,
            end = ReelsDimensions.categoryScreenHorizontalPadding,
            bottom = ReelsDimensions.categoryScreenBottomPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridHorizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridVerticalSpacing)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            MovieCategoryHeader(category = category, onBackClick = onBackClick)
        }

        items(CATEGORY_PLACEHOLDER_COUNT) {
            MovieCategoryPlaceholderCard()
        }
    }
}

@Composable
internal fun MovieCategoryErrorState(
    category: MovieCategory,
    retryLabel: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = ReelsDimensions.categoryScreenHorizontalPadding,
                top = ReelsDimensions.categoryScreenTopPadding,
                end = ReelsDimensions.categoryScreenHorizontalPadding,
                bottom = ReelsDimensions.categoryScreenBottomPadding
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.categoryGridVerticalSpacing)
    ) {
        MovieCategoryHeader(category = category, onBackClick = onBackClick)

        MovieCategoryStateContent(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(ReelsDimensions.categoryStateIconSize)
                )
            },
            title = "Couldn't load movies",
            message = "Check your connection and try again.",
            action = {
                Button(
                    modifier = Modifier.height(ReelsDimensions.categoryStateActionHeight),
                    onClick = onRetry,
                    shape = RoundedCornerShape(ReelsDimensions.categoryStateActionCornerSize),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(
                        horizontal = ReelsDimensions.categoryStateActionHorizontalPadding,
                        vertical = ReelsDimensions.categoryStateActionVerticalPadding
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            ReelsDimensions.categoryStateActionContentSpacing
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(ReelsDimensions.categoryStateActionIconSize)
                        )
                        Text(
                            text = retryLabel,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        )
    }
}

@Composable
internal fun MovieCategoryEmptyState(
    category: MovieCategory,
    modifier: Modifier = Modifier,
) {
    MovieCategoryStateContent(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Movie,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.categoryStateIconSize)
            )
        },
        title = "Nothing here yet",
        message = "Check back soon — new ${category.displayLabel.lowercase()} titles will appear here."
    )
}

@Composable
private fun MovieCategoryStateContent(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ReelsDimensions.categoryStateHorizontalPadding,
                vertical = ReelsDimensions.categoryStateVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.categoryStateIconContainerSize),
            shape = RoundedCornerShape(ReelsDimensions.categoryStateIconContainerCornerSize),
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) { icon() }
        }

        Text(
            modifier = Modifier.padding(top = ReelsDimensions.categoryStateIconToTitleSpacing),
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                lineHeight = 28.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )

        Text(
            modifier = Modifier.padding(top = ReelsDimensions.categoryStateTitleToMessageSpacing),
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 23.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        action?.let {
            Box(
                modifier = Modifier.padding(
                    top = ReelsDimensions.categoryStateMessageToActionSpacing
                )
            ) {
                it()
            }
        }
    }
}
