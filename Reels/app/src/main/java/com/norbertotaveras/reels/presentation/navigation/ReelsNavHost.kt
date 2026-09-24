/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.norbertotaveras.reels.presentation.features.cast.details.CastDetailsRoute
import com.norbertotaveras.reels.presentation.features.cast.filmography.FilmographyRoute
import com.norbertotaveras.reels.presentation.features.movies.discover.DiscoverRoute
import com.norbertotaveras.reels.presentation.features.movies.cast.MovieCastRoute
import com.norbertotaveras.reels.presentation.features.movies.category.MovieCategoryRoute
import com.norbertotaveras.reels.presentation.features.movies.details.MovieDetailsRoute
import com.norbertotaveras.reels.presentation.features.movies.favorites.FavoritesRoute
import com.norbertotaveras.reels.presentation.features.movies.list.MoviesListRoute
import com.norbertotaveras.reels.presentation.features.movies.search.SearchRoute
import com.norbertotaveras.reels.presentation.features.settings.SettingsRoute

@Composable
fun ReelsNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val openExternalUrl: (String) -> Unit = openExternalUrl@{ url ->
        val uri = Uri.parse(url)
        val isSupportedScheme = uri.scheme.equals("http", ignoreCase = true) ||
            uri.scheme.equals("https", ignoreCase = true)

        if (!isSupportedScheme) {
            Toast.makeText(context, "Unsupported link", Toast.LENGTH_SHORT).show()
            return@openExternalUrl
        }

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, "No app can open this link", Toast.LENGTH_SHORT).show()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

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
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ReelsDestinations.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ReelsDestinations.HOME) {
                MoviesListRoute(
                    onMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    },
                    onSeeAllClick = { category ->
                        navController.navigateToMovieCategory(category)
                    }
                )
            }

            composable(ReelsDestinations.DISCOVER) {
                DiscoverRoute(
                    onMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    }
                )
            }

            composable(
                route = ReelsDestinations.MOVIE_CATEGORY_ROUTE,
                arguments = listOf(
                    navArgument(ReelsDestinations.MOVIE_CATEGORY_ARG) {
                        type = NavType.StringType
                    }
                )
            ) {
                MovieCategoryRoute(
                    onBackClick = navController::navigateUp,
                    onMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    }
                )
            }

            composable(ReelsDestinations.SEARCH) {
                SearchRoute(
                    onMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    }
                )
            }

            composable(ReelsDestinations.FAVORITES) {
                FavoritesRoute(
                    onMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    },
                    onPersonClick = { personId ->
                        navController.navigateToCastDetails(personId)
                    }
                )
            }

            composable(ReelsDestinations.SETTINGS) {
                SettingsRoute(
                    onNavigateToLibrary = {
                        navController.navigateToBottomNavRoute(ReelsDestinations.FAVORITES)
                    },
                    onOpenExternalUrl = openExternalUrl
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
                        navController.navigateToCastDetails(personId)
                    },
                    onCastSeeAllClick = { movieId ->
                        navController.navigateToMovieCast(movieId)
                    },
                    onTrailerClick = openExternalUrl,
                    onSimilarMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    }
                )
            }

            composable(
                route = ReelsDestinations.MOVIE_CAST_ROUTE,
                arguments = listOf(
                    navArgument(ReelsDestinations.MOVIE_ID_ARG) {
                        type = NavType.IntType
                    }
                )
            ) {
                MovieCastRoute(
                    onBackClick = navController::navigateUp,
                    onCastClick = { personId ->
                        navController.navigateToCastDetails(personId)
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
            ) {
                CastDetailsRoute(
                    onBackClick = navController::navigateUp,
                    onFilmographyClick = { personId ->
                        navController.navigate(ReelsDestinations.filmographyRoute(personId))
                    },
                    onMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    }
                )
            }

            composable(
                route = ReelsDestinations.FILMOGRAPHY_ROUTE,
                arguments = listOf(
                    navArgument(ReelsDestinations.PERSON_ID_ARG) {
                        type = NavType.IntType
                    }
                )
            ) {
                FilmographyRoute(
                    onBackClick = navController::navigateUp,
                    onMovieClick = { movieId ->
                        navController.navigateToMovieDetails(movieId)
                    }
                )
            }
        }
    }
}

@Composable
private fun ReelsBottomNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
        )

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            reelsBottomNavItems.forEach { item ->
                val selected = currentRoute == item.route

                NavigationBarItem(
                    selected = selected,
                    onClick = { onItemClick(item.route) },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}
