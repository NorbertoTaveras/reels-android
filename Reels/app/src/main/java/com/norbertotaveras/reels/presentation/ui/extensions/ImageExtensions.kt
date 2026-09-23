/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import androidx.compose.runtime.Composable
import com.norbertotaveras.reels.BuildConfig
import com.norbertotaveras.reels.presentation.ui.theme.LocalImageQuality

@Composable
fun String?.toTmdbImageUrl(): String? {
    val imageQuality = LocalImageQuality.current
    return this
        ?.takeIf { path -> path.isNotBlank() }
        ?.let { path -> "${BuildConfig.BASE_IMAGE_URL}${imageQuality.tmdbSizePath}$path" }
}
