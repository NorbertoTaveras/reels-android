package com.norbertotaveras.reels.presentation.ui.extensions

fun Throwable.toUserMessage(fallback: String): String {
    return message
        ?.takeIf { it.isNotBlank() }
        ?: fallback
}
