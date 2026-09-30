/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import androidx.compose.runtime.Composable
import com.norbertotaveras.reels.BuildConfig
import com.norbertotaveras.reels.domain.model.settings.ImageQuality
import com.norbertotaveras.reels.presentation.ui.theme.LocalImageQuality

@Composable
fun String?.toTmdbImageUrl(
    imageType: TmdbImageType = TmdbImageType.Poster
): String? {
    val imageQuality = LocalImageQuality.current
    return this
        ?.takeIf { path -> path.isNotBlank() }
        ?.let { path -> "${BuildConfig.BASE_IMAGE_URL}${imageType.sizePath(imageQuality)}$path" }
}

private fun TmdbImageType.sizePath(imageQuality: ImageQuality): String {
    return when (this) {
        TmdbImageType.Poster -> when (imageQuality) {
            ImageQuality.High -> "w342"
            ImageQuality.Standard -> "w185"
            ImageQuality.Low -> "w92"
        }

        TmdbImageType.Backdrop -> when (imageQuality) {
            ImageQuality.High -> "w780"
            ImageQuality.Standard -> "w300"
            ImageQuality.Low -> "w300"
        }

        TmdbImageType.Profile -> when (imageQuality) {
            ImageQuality.High -> "w185"
            ImageQuality.Standard -> "w92"
            ImageQuality.Low -> "w45"
        }
    }
}
