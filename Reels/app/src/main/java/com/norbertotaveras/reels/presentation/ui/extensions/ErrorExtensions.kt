/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

fun Throwable.toUserMessage(fallback: String): String {
    return message
        ?.takeIf { it.isNotBlank() }
        ?: fallback
}

fun String.toErrorTitle(fallback: String): String {
    return if (isTmdbConfigurationMessage()) {
        "TMDB setup required"
    } else {
        fallback
    }
}

private fun String.isTmdbConfigurationMessage(): Boolean {
    return contains(
        other = "TMDB API token is missing",
        ignoreCase = true
    )
}
