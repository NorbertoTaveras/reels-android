/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.model.favorite.SavedPersonDataModel

data class FavoritesUiState(
    val movies: List<FavoriteMovieDataModel> = emptyList(),
    val people: List<SavedPersonDataModel> = emptyList(),
    val selectedTab: LibraryTab = LibraryTab.Movies,
    val isTmdbAccountConnected: Boolean = false,
    val removingMovieIds: Set<Int> = emptySet(),
    val removingPersonIds: Set<Int> = emptySet(),
)

enum class LibraryTab(
    val title: String,
) {
    Movies(title = "Movies"),
    People(title = "People"),
    Tmdb(title = "TMDB")
}

val FavoritesUiState.selectedItemCount: Int
    get() = when (selectedTab) {
        LibraryTab.Movies -> movies.size
        LibraryTab.People -> people.size
        LibraryTab.Tmdb -> 0
    }

val FavoritesUiState.hasSavedItems: Boolean
    get() = movies.isNotEmpty() || people.isNotEmpty()

val FavoritesUiState.hasSelectedTabItems: Boolean
    get() = selectedTab == LibraryTab.Tmdb || selectedItemCount > 0

val FavoritesUiState.offlineLabel: String
    get() = "Available offline"
