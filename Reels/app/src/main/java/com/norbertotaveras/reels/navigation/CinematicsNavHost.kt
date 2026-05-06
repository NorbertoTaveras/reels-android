package com.norbertotaveras.reels.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.norbertotaveras.reels.features.movies.details.MovieDetailsRoute
import com.norbertotaveras.reels.features.movies.list.MoviesListRoute

@Composable
fun CinematicsNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CinematicsDestinations.MOVIES,
        modifier = modifier
    ) {
        composable(CinematicsDestinations.MOVIES) {
            MoviesListRoute(
                onMovieClick = { movieId ->
                    navController.navigate(CinematicsDestinations.movieDetailsRoute(movieId))
                }
            )
        }

        composable(
            route = CinematicsDestinations.MOVIE_DETAILS_ROUTE,
            arguments = listOf(
                navArgument(CinematicsDestinations.MOVIE_ID_ARG) {
                    type = NavType.IntType
                }
            )
        ) {
            MovieDetailsRoute(onBackClick = navController::navigateUp)
        }
    }
}
