/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.norbertotaveras.reels.presentation.features.discover.DiscoverRoute
import com.norbertotaveras.reels.presentation.features.home.MoviesListRoute
import com.norbertotaveras.reels.presentation.features.library.FavoritesRoute
import com.norbertotaveras.reels.presentation.features.movie.cast.MovieCastRoute
import com.norbertotaveras.reels.presentation.features.movie.category.MovieCategoryRoute
import com.norbertotaveras.reels.presentation.features.movie.details.MovieDetailsRoute
import com.norbertotaveras.reels.presentation.features.person.details.CastDetailsRoute
import com.norbertotaveras.reels.presentation.features.person.filmography.FilmographyRoute
import com.norbertotaveras.reels.presentation.features.plans.overview.PlansRoute
import com.norbertotaveras.reels.presentation.features.search.SearchRoute
import com.norbertotaveras.reels.presentation.features.settings.SettingsRoute

@Composable
fun ReelsNavHost(
    modifier: Modifier = Modifier,
    tmdbAuthCallbackId: Long? = null,
    onTmdbAuthCallbackConsumed: (Long) -> Unit = {},
    deepLinkRequest: String? = null,
    onDeepLinkConsumed: (String) -> Unit = {},
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val openExternalUrl: (String) -> Unit = openExternalUrl@{ url ->
        val uri = url.toUri()
        val isSupportedScheme =
            uri.scheme.equals("http", ignoreCase = true) ||
                uri.scheme.equals("https", ignoreCase = true)

        if (!isSupportedScheme) {
            Toast.makeText(context, "Unsupported link", Toast.LENGTH_SHORT).show()
            return@openExternalUrl
        }

        val intent =
            Intent(Intent.ACTION_VIEW, uri).apply { addCategory(Intent.CATEGORY_BROWSABLE) }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No app can open this link", Toast.LENGTH_SHORT).show()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    LaunchedEffect(tmdbAuthCallbackId) {
        if (tmdbAuthCallbackId != null) {
            navController.navigateToBottomNavRoute(ReelsDestinations.SETTINGS)
        }
    }

    LaunchedEffect(deepLinkRequest) {
        val request = deepLinkRequest ?: return@LaunchedEffect
        when {
            request.startsWith("plans:") ->
                navController.navigate(ReelsDestinations.plansRoute(request.substringAfter(':')))
            request.startsWith("movie:") ->
                request
                    .substringAfter(':')
                    .toIntOrNull()
                    ?.let(navController::navigateToMovieDetails)
        }
        onDeepLinkConsumed(request)
    }

    val bottomNavRoutes = reelsBottomNavItems.map { item -> item.route }
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                ReelsBottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        if (route != currentRoute) {
                            navController.navigateToBottomNavRoute(route)
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ReelsDestinations.HOME,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
        ) {
            composable(ReelsDestinations.HOME) {
                MoviesListRoute(
                    onMovieClick = { movieId -> navController.navigateToMovieDetails(movieId) },
                    onSeeAllClick = { category -> navController.navigateToMovieCategory(category) },
                )
            }

            composable(ReelsDestinations.DISCOVER) {
                DiscoverRoute(
                    onMovieClick = { movieId -> navController.navigateToMovieDetails(movieId) }
                )
            }

            composable(
                route = ReelsDestinations.MOVIE_CATEGORY_ROUTE,
                arguments =
                    listOf(
                        navArgument(ReelsDestinations.MOVIE_CATEGORY_ARG) {
                            type = NavType.StringType
                        }
                    ),
            ) {
                MovieCategoryRoute(
                    onBackClick = navController::navigateUp,
                    onMovieClick = { movieId -> navController.navigateToMovieDetails(movieId) },
                )
            }

            composable(
                route = ReelsDestinations.PLANS_ROUTE,
                arguments =
                    listOf(
                        navArgument(ReelsDestinations.PLANS_TAB_ARG) {
                            type = NavType.StringType
                            defaultValue = "releases"
                        }
                    ),
            ) {
                PlansRoute(
                    onBrowseReleases = {
                        navController.navigateToBottomNavRoute(ReelsDestinations.SEARCH)
                    },
                    onBrowseMovies = {
                        navController.navigateToBottomNavRoute(ReelsDestinations.DISCOVER)
                    },
                    onBackClick = navController::navigateUp,
                    onMovieClick = navController::navigateToMovieDetails,
                )
            }

            composable(ReelsDestinations.SEARCH) {
                SearchRoute(
                    onMovieClick = { movieId -> navController.navigateToMovieDetails(movieId) }
                )
            }

            composable(ReelsDestinations.FAVORITES) {
                FavoritesRoute(
                    onMovieClick = { movieId -> navController.navigateToMovieDetails(movieId) },
                    onPersonClick = { personId -> navController.navigateToCastDetails(personId) },
                    onNavigateToSettings = {
                        navController.navigateToBottomNavRoute(ReelsDestinations.SETTINGS)
                    },
                )
            }

            composable(ReelsDestinations.SETTINGS) {
                SettingsRoute(
                    onNavigateToLibrary = {
                        navController.navigateToBottomNavRoute(ReelsDestinations.FAVORITES)
                    },
                    onNavigateToPlans = { navController.navigate(ReelsDestinations.plansRoute()) },
                    onOpenExternalUrl = openExternalUrl,
                    tmdbAuthCallbackId = tmdbAuthCallbackId,
                    onTmdbAuthCallbackConsumed = onTmdbAuthCallbackConsumed,
                )
            }

            composable(
                route = ReelsDestinations.MOVIE_DETAILS_ROUTE,
                arguments =
                    listOf(navArgument(ReelsDestinations.MOVIE_ID_ARG) { type = NavType.IntType }),
            ) {
                MovieDetailsRoute(
                    onBackClick = navController::navigateUp,
                    onCastClick = { personId -> navController.navigateToCastDetails(personId) },
                    onCastSeeAllClick = { movieId -> navController.navigateToMovieCast(movieId) },
                    onTrailerClick = openExternalUrl,
                    onSimilarMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    },
                )
            }

            composable(
                route = ReelsDestinations.MOVIE_CAST_ROUTE,
                arguments =
                    listOf(navArgument(ReelsDestinations.MOVIE_ID_ARG) { type = NavType.IntType }),
            ) {
                MovieCastRoute(
                    onBackClick = navController::navigateUp,
                    onCastClick = { personId -> navController.navigateToCastDetails(personId) },
                )
            }

            composable(
                route = ReelsDestinations.CAST_DETAILS_ROUTE,
                arguments =
                    listOf(navArgument(ReelsDestinations.PERSON_ID_ARG) { type = NavType.IntType }),
            ) {
                CastDetailsRoute(
                    onBackClick = navController::navigateUp,
                    onFilmographyClick = { personId ->
                        navController.navigate(ReelsDestinations.filmographyRoute(personId))
                    },
                    onMovieClick = { movieId -> navController.navigateToMovieDetails(movieId) },
                )
            }

            composable(
                route = ReelsDestinations.FILMOGRAPHY_ROUTE,
                arguments =
                    listOf(navArgument(ReelsDestinations.PERSON_ID_ARG) { type = NavType.IntType }),
            ) {
                FilmographyRoute(
                    onBackClick = navController::navigateUp,
                    onMovieClick = { movieId -> navController.navigateToMovieDetails(movieId) },
                )
            }
        }
    }
}
