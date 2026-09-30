/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.error

import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

fun Throwable.toTmdbNetworkException(): TmdbNetworkException {
    val userMessage = when (this) {
        is TmdbNetworkException -> message
        is TmdbConfigurationException -> message ?: "TMDB API token is missing. Check the app configuration."
        is TmdbSessionExpiredException -> message ?: "Your TMDB session expired. Connect again in Settings."
        is SocketTimeoutException -> "TMDB took too long to respond. Please try again."
        is IOException -> "Check your internet connection and try again."
        is HttpException -> toUserMessage()
        else -> "Something went wrong while loading movies. Please try again."
    }

    return TmdbNetworkException(
        message = userMessage,
        cause = this
    )
}

fun Throwable.isTmdbSessionExpiredError(): Boolean {
    return this is TmdbSessionExpiredException ||
        this is HttpException && code() in TMDB_SESSION_ERROR_CODES
}

private fun HttpException.toUserMessage(): String {
    return when (code()) {
        401, 403 -> "TMDB authorization failed. Check the app configuration."
        404 -> "This TMDB item could not be found."
        408 -> "TMDB took too long to respond. Please try again."
        429 -> "TMDB rate limit reached. Please wait a moment and try again."
        in 500..599 -> "TMDB is unavailable right now. Please try again later."
        else -> "Unable to load TMDB data. Please try again."
    }
}

private val TMDB_SESSION_ERROR_CODES = setOf(401, 403)
