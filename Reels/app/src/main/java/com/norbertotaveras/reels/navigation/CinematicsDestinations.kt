package com.norbertotaveras.reels.navigation

object CinematicsDestinations {
    const val MOVIES = "movies"
    const val MOVIE_DETAILS = "movieDetails"
    const val MOVIE_ID_ARG = "movieId"
    const val MOVIE_DETAILS_ROUTE = "$MOVIE_DETAILS/{$MOVIE_ID_ARG}"

    fun movieDetailsRoute(movieId: Int): String = "$MOVIE_DETAILS/$movieId"
}
