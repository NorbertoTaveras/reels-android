package com.norbertotaveras.reels.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.norbertotaveras.reels.features.cast.details.CastDetailsRoute
import com.norbertotaveras.reels.features.movies.details.MovieDetailsRoute
import com.norbertotaveras.reels.features.movies.list.MoviesListRoute

@Composable
fun ReelsNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val uriHandler = LocalUriHandler.current
    val openExternalUrl: (String) -> Unit = { url ->
        uriHandler.openUri(url)
    }


    NavHost(
        navController = navController,
        startDestination = ReelsDestinations.MOVIES,
        modifier = modifier
    ) {
        composable(ReelsDestinations.MOVIES) {
            MoviesListRoute(
                onMovieClick = { movieId ->
                    navController.navigate(ReelsDestinations.movieDetailsRoute(movieId))
                }
            )
        }

        composable(
            route = ReelsDestinations.MOVIE_DETAILS_ROUTE,
            arguments = listOf(
                navArgument(ReelsDestinations.MOVIE_ID_ARG) {
                    type = NavType.IntType
                }
            )
        ) {
            MovieDetailsRoute(
                onBackClick = navController::navigateUp,
                onCastClick = { personId ->
                    navController.navigate(ReelsDestinations.castDetailsRoute(personId))
                },
                onTrailerClick = openExternalUrl,
                onSimilarMovieClick = { movieId ->
                    navController.navigate(ReelsDestinations.movieDetailsRoute(movieId))
                }
            )
        }

        composable(
            route = ReelsDestinations.CAST_DETAILS_ROUTE,
            arguments = listOf(
                navArgument(ReelsDestinations.PERSON_ID_ARG) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val personId = checkNotNull(
                backStackEntry.arguments?.getInt(ReelsDestinations.PERSON_ID_ARG)
            )

            CastDetailsRoute(
                onBackClick = navController::navigateUp,
                onMovieClick = { movieId ->
                    navController.navigate(ReelsDestinations.movieDetailsRoute(movieId))
                }
            )
        }
    }
}
