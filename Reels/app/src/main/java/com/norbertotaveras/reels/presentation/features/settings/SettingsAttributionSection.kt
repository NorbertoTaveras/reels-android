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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertotaveras.reels.R
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
internal fun TmdbAttributionSection(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
    ) {
        Text(
            text = "TMDB ATTRIBUTION",
            modifier = Modifier.padding(
                horizontal = ReelsDimensions.settingsSectionTitleHorizontalPadding
            ),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = ReelsDimensions.settingsSectionTitleSize,
                letterSpacing = 0.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        TmdbAttributionCard(onClick = onClick)
    }
}

@Composable
private fun TmdbAttributionCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(ReelsDimensions.settingsTmdbCardPadding),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.large)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TmdbLogoMark()

            Spacer(modifier = Modifier.width(ReelsSpacing.large))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "The Movie Database",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Powered by TMDB API",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "This product uses the TMDB API but is not endorsed or certified by TMDB.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Visit themoviedb.org",
                style = MaterialTheme.typography.bodySmall,
                color = ReelsAccent,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsExternalIconSize),
                tint = ReelsAccent
            )
        }
    }
}

@Composable
internal fun TmdbLogoMark(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(ReelsDimensions.settingsTmdbLogoSize)
            .clip(MaterialTheme.shapes.large),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Image(
            painter = painterResource(id = R.drawable.tmdb_logo),
            contentDescription = "TMDB logo",
            modifier = Modifier
                .fillMaxSize()
                .padding(ReelsSpacing.small),
            contentScale = ContentScale.Fit
        )
    }
}
