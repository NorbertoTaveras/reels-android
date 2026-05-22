package com.norbertotaveras.reels.presentation.navigation

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.norbertotaveras.reels.presentation.features.cast.details.CastDetailsRoute
import com.norbertotaveras.reels.presentation.features.movies.details.MovieDetailsRoute
import com.norbertotaveras.reels.presentation.features.movies.favorites.FavoritesRoute
import com.norbertotaveras.reels.presentation.features.movies.list.MoviesListRoute
import com.norbertotaveras.reels.presentation.features.movies.search.SearchRoute
import com.norbertotaveras.reels.presentation.features.settings.SettingsScreen

@Composable
fun ReelsNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val uriHandler = LocalUriHandler.current
    val openExternalUrl: (String) -> Unit = { url ->
        uriHandler.openUri(url)
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
                            navController.navigate(route) {
                                popUpTo(ReelsDestinations.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
                        navController.navigate(ReelsDestinations.movieDetailsRoute(movieId))
                    }
                )
            }

            composable(ReelsDestinations.SEARCH) {
                SearchRoute(
                    onMovieClick = { movieId ->
                        navController.navigate(ReelsDestinations.movieDetailsRoute(movieId))
                    }
                )
            }

            composable(ReelsDestinations.FAVORITES) {
                FavoritesRoute(
                    onMovieClick = { movieId ->
                        navController.navigate(ReelsDestinations.movieDetailsRoute(movieId))
                    }
                )
            }

            composable(ReelsDestinations.SETTINGS) {
                SettingsScreen()
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
            ) {
                CastDetailsRoute(
                    onBackClick = navController::navigateUp,
                    onMovieClick = { movieId ->
                        navController.navigate(ReelsDestinations.movieDetailsRoute(movieId))
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
