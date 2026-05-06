package com.norbertotaveras.reels.ui.extensions

import com.norbertotaveras.reels.BuildConfig

fun String?.toTmdbImageUrl(): String? {
    return this?.takeIf { it.isNotBlank() }?.let { "${BuildConfig.BASE_IMAGE_URL}$it" }
}
