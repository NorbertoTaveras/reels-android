/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import java.util.Locale

internal fun Long.toStorageSizeLabel(): String {
    val bytes = coerceAtLeast(0L)
    return when {
        bytes >= BYTES_PER_GIGABYTE -> formatStorageSize(
            value = bytes.toDouble() / BYTES_PER_GIGABYTE,
            unit = "GB"
        )
        bytes >= BYTES_PER_MEGABYTE -> formatStorageSize(
            value = bytes.toDouble() / BYTES_PER_MEGABYTE,
            unit = "MB"
        )
        bytes >= BYTES_PER_KILOBYTE -> formatStorageSize(
            value = bytes.toDouble() / BYTES_PER_KILOBYTE,
            unit = "KB"
        )
        else -> "$bytes B"
    }
}

private fun formatStorageSize(
    value: Double,
    unit: String,
): String {
    val format = if (value >= 10.0) "%.0f %s" else "%.1f %s"
    return String.format(Locale.US, format, value, unit)
}

private const val BYTES_PER_KILOBYTE = 1_024L
private const val BYTES_PER_MEGABYTE = 1_024 * BYTES_PER_KILOBYTE
private const val BYTES_PER_GIGABYTE = 1_024 * BYTES_PER_MEGABYTE
