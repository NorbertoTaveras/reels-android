/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun CastHeroHeader(
    name: String,
    profileUrl: String?,
    knownForDepartment: String?,
    isFavorite: Boolean,
    isFavoriteLoading: Boolean,
    isOfflineSavedPerson: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ReelsDimensions.castDetailsHeroHeight)
    ) {
        AsyncImage(
            model = profileUrl,
            contentDescription = name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.30f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        FilledIconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = ReelsSpacing.screenHorizontal,
                    top = ReelsDimensions.castDetailsHeroActionTopPadding
                )
                .size(ReelsDimensions.castDetailsHeroActionButtonSize),
            onClick = onBackClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate back",
                modifier = Modifier.size(ReelsDimensions.castDetailsHeroActionIconSize)
            )
        }

        FilledIconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    end = ReelsSpacing.screenHorizontal,
                    top = ReelsDimensions.castDetailsHeroActionTopPadding
                )
                .size(ReelsDimensions.castDetailsHeroActionButtonSize)
                .then(
                    if (isFavorite) {
                        Modifier.border(
                            width = 1.dp,
                            color = ReelsAccent.copy(alpha = 0.40f),
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ),
            enabled = !isFavoriteLoading,
            onClick = onFavoriteClick,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isFavorite) {
                    ReelsAccent.copy(alpha = 0.20f)
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
                },
                contentColor = if (isFavorite) {
                    ReelsAccent
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        ) {
            if (isFavoriteLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(ReelsDimensions.castDetailsDepartmentIconSize),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = if (isFavorite) {
                        "Remove from library"
                    } else {
                        "Save to library"
                    },
                    modifier = Modifier.size(ReelsDimensions.castDetailsHeroActionIconSize)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = ReelsSpacing.screenHorizontal)
                .padding(bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
        ) {
            if (isOfflineSavedPerson) {
                CastOfflineStatusBadge()
            }

            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (!knownForDepartment.isNullOrBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Work,
                        contentDescription = null,
                        modifier = Modifier.size(ReelsDimensions.castDetailsDepartmentIconSize),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = knownForDepartment,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isOfflineSavedPerson) {
                        Text(
                            text = "Latest details unavailable offline",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                fontStyle = FontStyle.Italic
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

        }
    }
}
