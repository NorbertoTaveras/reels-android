/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTeal
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTmdbBlue

@Composable
internal fun TmdbAccountHeaderRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TmdbLogoMark()

        Spacer(modifier = Modifier.width(ReelsSpacing.large))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = ReelsDimensions.settingsTmdbConnectedTitleSize,
                    lineHeight = ReelsDimensions.settingsTmdbConnectedTitleLineHeight,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = ReelsDimensions.settingsTmdbConnectedBodySize,
                    lineHeight = ReelsDimensions.settingsTmdbConnectedBodyLineHeight,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (trailing != null) {
            Spacer(modifier = Modifier.width(ReelsSpacing.medium))
            trailing()
        }
    }
}

@Composable
internal fun TmdbConnectedAccountHeaderRow(
    accountName: String,
    accountUrl: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(ReelsDimensions.settingsTmdbConnectedRowPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            ReelsDimensions.settingsTmdbConnectedRowSpacing
        )
    ) {
        Box(
            modifier = Modifier
                .size(ReelsDimensions.settingsTmdbConnectedAvatarSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = ReelsDimensions.settingsTmdbConnectedAvatarBorderWidth,
                    color = ReelsTmdbBlue.copy(alpha = 0.40f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsTmdbConnectedIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (avatarUrl != null) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "$accountName avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            TmdbConnectedText(
                text = accountName,
                color = MaterialTheme.colorScheme.onSurface
            )
            TmdbConnectedSupportingText(text = accountUrl)
        }

        ConnectedChip()
    }
}

@Composable
internal fun TmdbAccountStatusRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val rowModifier = modifier
        .fillMaxWidth()
        .then(
            if (onClick != null) {
                Modifier.clickable(onClick = onClick)
            } else {
                Modifier
            }
        )

    Row(
        modifier = rowModifier.padding(ReelsDimensions.settingsTmdbConnectedRowPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            ReelsDimensions.settingsTmdbConnectedRowSpacing
        )
    ) {
        Box(
            modifier = Modifier
                .size(ReelsDimensions.settingsTmdbConnectedIconContainerSize)
                .clip(RoundedCornerShape(ReelsDimensions.settingsTmdbConnectedIconCornerSize))
                .background(iconContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsTmdbConnectedIconSize),
                tint = iconTint
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TmdbConnectedText(text = title, color = titleColor)
            TmdbConnectedSupportingText(text = subtitle)
        }

        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
internal fun ConnectedChip(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = ReelsTeal.copy(alpha = 0.16f),
        contentColor = ReelsTeal
    ) {
        Text(
            text = "Connected",
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.settingsTmdbConnectedChipHorizontalPadding,
                vertical = ReelsDimensions.settingsTmdbConnectedChipVerticalPadding
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = ReelsDimensions.settingsTmdbConnectedChipTextSize,
                lineHeight = ReelsDimensions.settingsTmdbConnectedChipLineHeight,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.sp
            )
        )
    }
}

@Composable
internal fun StaticSettingsSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(
            width = ReelsDimensions.settingsTmdbConnectedSwitchWidth,
            height = ReelsDimensions.settingsTmdbConnectedSwitchHeight
        ),
        shape = MaterialTheme.shapes.large,
        color = if (checked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(ReelsDimensions.settingsTmdbConnectedSwitchThumbSize)
                    .clip(MaterialTheme.shapes.large)
                    .background(if (checked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

@Composable
internal fun TmdbSyncStatusChevron() {
    Icon(
        imageVector = Icons.Filled.ChevronRight,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun TmdbConnectedText(
    text: String,
    color: Color,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = ReelsDimensions.settingsTmdbConnectedTitleSize,
            lineHeight = ReelsDimensions.settingsTmdbConnectedTitleLineHeight,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp
        ),
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun TmdbConnectedSupportingText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = ReelsDimensions.settingsTmdbConnectedBodySize,
            lineHeight = ReelsDimensions.settingsTmdbConnectedBodyLineHeight,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
