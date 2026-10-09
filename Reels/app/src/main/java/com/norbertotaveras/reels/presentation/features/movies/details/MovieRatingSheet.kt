/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsRatingContainerColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieRatingSheet(
    movieTitle: String,
    selectedRating: Double?,
    isLoading: Boolean,
    onRatingSelected: (Double) -> Unit,
    onSubmitClick: () -> Unit,
    onClearClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(
            topStart = ReelsDimensions.ratingSheetCornerSize,
            topEnd = ReelsDimensions.ratingSheetCornerSize
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = ReelsDimensions.ratingSheetDragHandleTopPadding,
                        bottom = ReelsDimensions.ratingSheetDragHandleBottomPadding
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(ReelsDimensions.ratingSheetDragHandleWidth)
                        .height(ReelsDimensions.ratingSheetDragHandleHeight)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.30f),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        },
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ReelsSpacing.screenHorizontal,
                        vertical = ReelsDimensions.ratingSheetHeaderVerticalPadding
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Rate this movie",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = movieTitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (selectedRating != null) {
                    Text(
                        text = "Clear",
                        modifier = Modifier.clickable(
                            enabled = !isLoading,
                            role = Role.Button,
                            onClick = onClearClick
                        ),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal)
                    .padding(
                        top = ReelsDimensions.ratingSheetBodyTopPadding,
                        bottom = ReelsDimensions.ratingSheetBottomPadding
                    )
            ) {
                selectedRating?.let { rating ->
                    SelectedRatingSummary(rating = rating)
                    Spacer(
                        modifier = Modifier.height(
                            ReelsDimensions.ratingSheetSummaryBottomSpacing
                        )
                    )
                }

                RatingChoiceGrid(
                    selectedRating = selectedRating,
                    enabled = !isLoading,
                    onRatingSelected = onRatingSelected
                )

                Spacer(modifier = Modifier.height(ReelsDimensions.ratingSheetGridBottomSpacing))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ReelsDimensions.ratingSheetSubmitButtonHeight),
                    enabled = selectedRating != null && !isLoading,
                    onClick = onSubmitClick,
                    shape = RoundedCornerShape(ReelsDimensions.ratingSheetChoiceCornerSize),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(ReelsDimensions.ratingSheetProgressSize),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = selectedRating?.let { rating ->
                                "Submit ${rating.toRatingText()}/10 Rating"
                            } ?: "Tap a number to rate",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedRatingSummary(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            modifier = Modifier.size(ReelsDimensions.ratingSheetSummaryIconSize),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = rating.toRatingText(),
            modifier = Modifier.padding(start = ReelsSpacing.xSmall),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = " / 10",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                lineHeight = 28.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RatingChoiceGrid(
    selectedRating: Double?,
    enabled: Boolean,
    onRatingSelected: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        RatingChoiceRow(
            ratings = 1..5,
            selectedRating = selectedRating,
            enabled = enabled,
            onRatingSelected = onRatingSelected
        )
        RatingChoiceRow(
            ratings = 6..10,
            selectedRating = selectedRating,
            enabled = enabled,
            onRatingSelected = onRatingSelected
        )
    }
}

@Composable
private fun RatingChoiceRow(
    ratings: IntRange,
    selectedRating: Double?,
    enabled: Boolean,
    onRatingSelected: (Double) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        ratings.forEach { rating ->
            RatingChoice(
                rating = rating,
                selectedRating = selectedRating,
                selected = rating.toDouble() == selectedRating,
                enabled = enabled,
                onClick = { onRatingSelected(rating.toDouble()) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RatingChoice(
    rating: Int,
    selectedRating: Double?,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(ReelsDimensions.ratingSheetChoiceHeight)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        shape = RoundedCornerShape(ReelsDimensions.ratingSheetChoiceCornerSize),
        color = ratingChoiceContainerColor(
            rating = rating,
            selectedRating = selectedRating,
            selected = selected
        ),
        contentColor = ratingChoiceContentColor(
            rating = rating,
            selectedRating = selectedRating,
            selected = selected
        ),
        border = BorderStroke(
            width = 1.dp,
            color = ratingChoiceBorderColor(
                rating = rating,
                selectedRating = selectedRating,
                selected = selected
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = rating.toString(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ratingChoiceContainerColor(
    rating: Int,
    selectedRating: Double?,
    selected: Boolean,
) = when {
    selected -> MaterialTheme.colorScheme.primary
    selectedRating != null && rating.toDouble() < selectedRating -> {
        reelsRatingContainerColor()
    }
    else -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun ratingChoiceContentColor(
    rating: Int,
    selectedRating: Double?,
    selected: Boolean,
) = when {
    selected -> MaterialTheme.colorScheme.onPrimary
    selectedRating != null && rating.toDouble() < selectedRating -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun ratingChoiceBorderColor(
    rating: Int,
    selectedRating: Double?,
    selected: Boolean,
) = when {
    selected -> MaterialTheme.colorScheme.primary
    selectedRating != null && rating.toDouble() < selectedRating -> {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
    }
    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
}
