/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

sealed interface FavoritesEvent {
    data class TabSelected(val tab: LibraryTab) : FavoritesEvent
    data class MovieClicked(val movieId: Int) : FavoritesEvent
    data class PersonClicked(val personId: Int) : FavoritesEvent
    data class FavoriteClicked(val movieId: Int) : FavoritesEvent
    data class SavedPersonClicked(val personId: Int) : FavoritesEvent
    data class WatchlistMovieRemoved(val movieId: Int) : FavoritesEvent
    data class RatedMovieRemoved(val movieId: Int) : FavoritesEvent
    data object ConnectSettingsClicked : FavoritesEvent
    data object RetryLibraryClicked : FavoritesEvent
}
