/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.cast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.person.model.CastMemberDataModel
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.reelsMutedContainerColor

@Composable
internal fun MovieCastRow(
    castMember: CastMemberDataModel,
    isSaved: Boolean,
    isSaving: Boolean,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(ReelsDimensions.fullCastRowCornerRadius),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = ReelsDimensions.fullCastBorderWidth,
            color = if (isSaved) {
                ReelsAccent.copy(alpha = 0.55f)
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.fullCastRowHorizontalPadding,
                vertical = ReelsDimensions.fullCastRowVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.fullCastRowContentSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(ReelsDimensions.fullCastProfileSize)
                    .clip(RoundedCornerShape(ReelsDimensions.fullCastProfileCornerRadius))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val profileUrl = castMember.profilePath.toTmdbImageUrl(
                    imageType = TmdbImageType.Profile
                )

                if (profileUrl.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    AsyncImage(
                        model = profileUrl,
                        contentDescription = castMember.name,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.fullCastRowTextSpacing)
            ) {
                Text(
                    text = castMember.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = castMember.characterName.ifBlank { "Character unavailable" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                modifier = Modifier
                    .size(ReelsDimensions.fullCastSaveButtonSize)
                    .clickable(
                        enabled = !isSaving,
                        role = Role.Button,
                        onClickLabel = if (isSaved) {
                            "Remove from library"
                        } else {
                            "Save to library"
                        },
                        onClick = onSaveClick
                    ),
                shape = RoundedCornerShape(ReelsDimensions.fullCastSaveButtonCornerRadius),
                color = if (isSaved) {
                    ReelsAccent.copy(alpha = 0.20f)
                } else {
                    reelsMutedContainerColor()
                },
                contentColor = if (isSaved) {
                    ReelsAccent
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                border = BorderStroke(
                    width = ReelsDimensions.fullCastBorderWidth,
                    color = if (isSaved) {
                        ReelsAccent
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = if (isSaved) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                            contentDescription = null,
                            modifier = Modifier.size(ReelsDimensions.fullCastSaveIconSize)
                        )
                    }
                }
            }
        }
    }
}
