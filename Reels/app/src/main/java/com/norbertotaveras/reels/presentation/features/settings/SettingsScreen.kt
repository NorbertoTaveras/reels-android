package com.norbertotaveras.reels.presentation.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.theme.ReelsAccent
import com.norbertotaveras.reels.presentation.ui.theme.ReelsBorder
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSurfaceHigh
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ReelsSpacing.screenHorizontal)
            .padding(top = ReelsSpacing.xLarge, bottom = ReelsSpacing.xxLarge),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xLarge)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        SettingsSection(title = "APP PREFERENCES") {
            SettingsRow(
                icon = Icons.Filled.HighQuality,
                title = "Image Quality",
                subtitle = "High quality posters and backdrops"
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.Language,
                title = "Language & Region",
                subtitle = "English · United States"
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.Palette,
                title = "Theme",
                subtitle = "Use system setting"
            )
        }

        SettingsSection(title = "DATA & STORAGE") {
            SettingsRow(
                icon = Icons.Filled.Download,
                title = "Saved Movies",
                subtitle = "Manage movies saved on this device"
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.DeleteOutline,
                title = "Clear Image Cache",
                subtitle = "Free up locally cached poster storage"
            )
        }

        SettingsSection(title = "ABOUT REELS") {
            SettingsRow(
                icon = Icons.Filled.Info,
                title = "App Version",
                subtitle = "1.0.0",
                showsChevron = false
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.Shield,
                title = "Privacy",
                subtitle = "Review app data practices"
            )

            SettingsDivider()

            SettingsRow(
                icon = Icons.Filled.Policy,
                title = "TMDB Attribution",
                subtitle = "Movie data provided by The Movie Database"
            )
        }

        TmdbAttributionNotice()
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = ReelsSpacing.xSmall),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = ReelsBorder,
                    shape = MaterialTheme.shapes.large
                ),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    showsChevron: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(horizontal = ReelsSpacing.medium, vertical = ReelsSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(ReelsSurfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = ReelsAccent
            )
        }

        Spacer(modifier = Modifier.width(ReelsSpacing.medium))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (showsChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier.padding(start = 60.dp),
        thickness = 1.dp,
        color = ReelsBorder
    )
}

@Composable
private fun TmdbAttributionNotice(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = ReelsBorder,
                shape = MaterialTheme.shapes.large
            )
            .padding(ReelsSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ReelsAccent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TM",
                style = MaterialTheme.typography.labelMedium,
                color = ReelsAccent,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(ReelsSpacing.medium))

        Text(
            text = "This product uses the TMDB API but is not endorsed or certified by TMDB.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    ReelsTheme {
        SettingsScreen()
    }
}
