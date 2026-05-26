package com.norbertotaveras.reels.data.remote.error

import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

class TmdbNetworkException(
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

fun Throwable.toTmdbNetworkException(): TmdbNetworkException {
    val userMessage = when (this) {
        is TmdbNetworkException -> message
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
