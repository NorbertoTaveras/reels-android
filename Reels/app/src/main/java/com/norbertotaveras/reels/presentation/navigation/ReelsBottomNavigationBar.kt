/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.reelsBorderColor

@Composable
internal fun ReelsBottomNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(
            thickness = ReelsDimensions.bottomNavigationDividerThickness,
            color = reelsBorderColor()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ReelsDimensions.bottomNavigationContentHeight)
                .padding(
                    horizontal = ReelsDimensions.bottomNavigationHorizontalPadding,
                    vertical = ReelsDimensions.bottomNavigationVerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            reelsBottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                val contentColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                ReelsBottomNavigationItem(
                    item = item,
                    selected = selected,
                    contentColor = contentColor,
                    onClick = { onItemClick(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ReelsBottomNavigationItem(
    item: ReelsBottomNavItem,
    selected: Boolean,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(ReelsDimensions.bottomNavigationItemCornerSize))
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab
            )
            .padding(
                horizontal = ReelsDimensions.bottomNavigationItemHorizontalPadding,
                vertical = ReelsDimensions.bottomNavigationItemVerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            ReelsDimensions.bottomNavigationItemSpacing,
            Alignment.CenterVertically
        )
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            modifier = Modifier.size(ReelsDimensions.bottomNavigationIconSize),
            tint = contentColor
        )

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = ReelsDimensions.bottomNavigationLabelSize,
                lineHeight = ReelsDimensions.bottomNavigationLabelLineHeight,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.sp
            ),
            color = contentColor,
            maxLines = 1
        )
    }
}
