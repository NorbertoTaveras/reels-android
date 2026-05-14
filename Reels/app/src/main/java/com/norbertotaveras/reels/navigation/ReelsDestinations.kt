package com.norbertotaveras.reels.navigation

object ReelsDestinations {
    const val HOME = "home"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val MOVIE_DETAILS = "movieDetails"
    const val MOVIE_ID_ARG = "movieId"
    const val MOVIE_DETAILS_ROUTE = "$MOVIE_DETAILS/{$MOVIE_ID_ARG}"
    const val PERSON_ID_ARG = "personId"
    const val CAST_DETAILS = "castDetails"
    const val CAST_DETAILS_ROUTE = "$CAST_DETAILS/{$PERSON_ID_ARG}"
    fun movieDetailsRoute(movieId: Int): String = "$MOVIE_DETAILS/$movieId"
    fun castDetailsRoute(personId: Int): String = "$CAST_DETAILS/$personId"

}
