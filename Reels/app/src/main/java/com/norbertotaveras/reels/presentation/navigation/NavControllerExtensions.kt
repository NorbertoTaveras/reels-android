/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.navigation

import androidx.navigation.NavHostController

internal fun NavHostController.navigateToBottomNavRoute(route: String) {
    navigate(route) {
        popUpTo(ReelsDestinations.HOME) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

internal fun NavHostController.navigateToMovieDetails(movieId: Int) {
    navigateIfNotCurrent(
        route = ReelsDestinations.movieDetailsRoute(movieId),
        destinationRoute = ReelsDestinations.MOVIE_DETAILS_ROUTE,
        argumentName = ReelsDestinations.MOVIE_ID_ARG,
        argumentValue = movieId
    )
}

internal fun NavHostController.navigateToMovieCategory(category: String) {
    navigateIfNotCurrent(
        route = ReelsDestinations.movieCategoryRoute(category),
        destinationRoute = ReelsDestinations.MOVIE_CATEGORY_ROUTE,
        argumentName = ReelsDestinations.MOVIE_CATEGORY_ARG,
        argumentValue = category
    )
}

internal fun NavHostController.navigateToMovieCast(movieId: Int) {
    navigateIfNotCurrent(
        route = ReelsDestinations.movieCastRoute(movieId),
        destinationRoute = ReelsDestinations.MOVIE_CAST_ROUTE,
        argumentName = ReelsDestinations.MOVIE_ID_ARG,
        argumentValue = movieId
    )
}

internal fun NavHostController.navigateToCastDetails(personId: Int) {
    navigateIfNotCurrent(
        route = ReelsDestinations.castDetailsRoute(personId),
        destinationRoute = ReelsDestinations.CAST_DETAILS_ROUTE,
        argumentName = ReelsDestinations.PERSON_ID_ARG,
        argumentValue = personId
    )
}

private fun NavHostController.navigateIfNotCurrent(
    route: String,
    destinationRoute: String,
    argumentName: String,
    argumentValue: Int,
) {
    val currentEntry = currentBackStackEntry
    val isCurrentDestination = currentEntry?.destination?.route == destinationRoute
    val isCurrentArgument = currentEntry?.arguments?.getInt(argumentName) == argumentValue

    if (isCurrentDestination && isCurrentArgument) return

    navigate(route)
}

private fun NavHostController.navigateIfNotCurrent(
    route: String,
    destinationRoute: String,
    argumentName: String,
    argumentValue: String,
) {
    val currentEntry = currentBackStackEntry
    val isCurrentDestination = currentEntry?.destination?.route == destinationRoute
    val isCurrentArgument = currentEntry?.arguments?.getString(argumentName) == argumentValue

    if (isCurrentDestination && isCurrentArgument) return

    navigate(route)
}
