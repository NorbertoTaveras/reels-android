/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.settings

import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import kotlin.math.roundToInt

@Composable
internal fun SettingsDialog(
    icon: ImageVector,
    title: String,
    onDismiss: () -> Unit,
    confirmLabel: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String? = null,
    destructive: Boolean = false,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SettingsDialogWindowEffects()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background.copy(
                        alpha = ReelsDimensions.settingsDialogBackdropAlpha
                    )
                )
                .padding(horizontal = ReelsSpacing.xLarge),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = modifier
                    .widthIn(max = ReelsDimensions.settingsDialogMaxWidth)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(ReelsDimensions.settingsDialogCornerSize),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    ReelsDimensions.settingsDialogBorderWidth,
                    MaterialTheme.colorScheme.outline
                ),
                shadowElevation = ReelsSpacing.xLarge
            ) {
                Column {
                    DialogIcon(
                        icon = icon,
                        destructive = destructive,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = ReelsSpacing.xLarge)
                    )

                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = ReelsSpacing.xLarge,
                                top = ReelsSpacing.medium,
                                end = ReelsSpacing.xLarge,
                                bottom = ReelsSpacing.xSmall
                            ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = ReelsDimensions.settingsDialogTitleSize,
                            lineHeight = ReelsDimensions.settingsDialogTitleLineHeight,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier.padding(
                            horizontal = ReelsSpacing.xLarge,
                            vertical = ReelsSpacing.medium
                        )
                    ) {
                        content()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = ReelsSpacing.large,
                                top = ReelsSpacing.small,
                                end = ReelsSpacing.large,
                                bottom = ReelsDimensions.settingsDialogActionBottomPadding
                            ),
                        horizontalArrangement = Arrangement.spacedBy(
                            ReelsSpacing.xSmall,
                            Alignment.End
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        dismissLabel?.let { label ->
                            SettingsDialogAction(
                                label = label,
                                onClick = onDismiss
                            )
                        }

                        SettingsDialogAction(
                            label = confirmLabel,
                            onClick = onConfirm,
                            destructive = destructive
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsDialogWindowEffects() {
    val view = LocalView.current
    val blurRadius = with(LocalDensity.current) {
        ReelsDimensions.settingsDialogBackdropBlurRadius.toPx().roundToInt()
    }

    DisposableEffect(view, blurRadius) {
        val window = (view.parent as? DialogWindowProvider)?.window

        window?.setDimAmount(0f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            window?.attributes = window?.attributes?.apply {
                blurBehindRadius = blurRadius
            }
        }

        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window?.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            }
        }
    }
}

@Composable
private fun SettingsDialogAction(
    label: String,
    onClick: () -> Unit,
    destructive: Boolean = false,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.height(ReelsDimensions.settingsDialogActionHeight),
        shape = RoundedCornerShape(ReelsDimensions.settingsDialogActionCornerSize),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (destructive) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        ),
        contentPadding = PaddingValues(horizontal = ReelsSpacing.large)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = ReelsDimensions.settingsDialogActionTextSize,
                lineHeight = ReelsDimensions.settingsDialogActionLineHeight,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
internal fun DialogOptionIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(ReelsDimensions.settingsDialogOptionIconContainerSize)
            .clip(RoundedCornerShape(ReelsDimensions.settingsDialogOptionIconCornerSize))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ReelsDimensions.settingsDialogOptionIconSize),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
internal fun SettingsDialogRadio(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(ReelsDimensions.settingsDialogRadioSize)
            .border(
                width = ReelsDimensions.settingsDialogRadioBorderWidth,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(ReelsDimensions.settingsDialogRadioDotSize)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Composable
internal fun SettingsInfoBanner(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ReelsDimensions.settingsDialogOptionCornerSize),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(ReelsSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(
                ReelsDimensions.settingsDialogInfoSpacing
            ),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = ReelsDimensions.settingsDialogInfoIconTopPadding)
                    .size(ReelsDimensions.settingsDialogInfoIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = ReelsDimensions.settingsDialogOptionBodySize,
                    lineHeight = ReelsDimensions.settingsDialogInfoLineHeight
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun DialogIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    val contentColor = if (destructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .size(ReelsDimensions.settingsDialogIconContainerSize)
            .clip(RoundedCornerShape(ReelsDimensions.settingsDialogIconCornerSize))
            .background(contentColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ReelsDimensions.settingsDialogIconSize),
            tint = contentColor
        )
    }
}

@Composable
internal fun CacheBreakdownRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(ReelsSpacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(ReelsDimensions.settingsDialogInfoIconSize),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = ReelsDimensions.settingsDialogOptionBodySize,
                    lineHeight = ReelsDimensions.settingsDialogOptionBodyLineHeight,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = ReelsDimensions.settingsDialogOptionBodySize,
                lineHeight = ReelsDimensions.settingsDialogOptionBodyLineHeight
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
