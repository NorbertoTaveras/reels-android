/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.navigation

object ReelsDestinations {
    const val HOME = "home"
    const val DISCOVER = "discover"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val MOVIE_DETAILS = "movieDetails"
    const val MOVIE_ID_ARG = "movieId"
    const val MOVIE_DETAILS_ROUTE = "$MOVIE_DETAILS/{$MOVIE_ID_ARG}"
    const val MOVIE_CATEGORY = "movieCategory"
    const val MOVIE_CATEGORY_ARG = "category"
    const val MOVIE_CATEGORY_ROUTE = "$MOVIE_CATEGORY/{$MOVIE_CATEGORY_ARG}"
    const val MOVIE_CAST = "movieCast"
    const val MOVIE_CAST_ROUTE = "$MOVIE_CAST/{$MOVIE_ID_ARG}"
    const val PERSON_ID_ARG = "personId"
    const val CAST_DETAILS = "castDetails"
    const val CAST_DETAILS_ROUTE = "$CAST_DETAILS/{$PERSON_ID_ARG}"
    const val FILMOGRAPHY = "filmography"
    const val FILMOGRAPHY_ROUTE = "$FILMOGRAPHY/{$PERSON_ID_ARG}"
    fun movieDetailsRoute(movieId: Int): String = "$MOVIE_DETAILS/$movieId"
    fun movieCategoryRoute(category: String): String = "$MOVIE_CATEGORY/$category"
    fun movieCastRoute(movieId: Int): String = "$MOVIE_CAST/$movieId"
    fun castDetailsRoute(personId: Int): String = "$CAST_DETAILS/$personId"
    fun filmographyRoute(personId: Int): String = "$FILMOGRAPHY/$personId"
}
