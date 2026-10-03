/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.model.favorite.SavedPersonDataModel

data class FavoritesUiState(
    val movies: List<FavoriteMovieDataModel> = emptyList(),
    val people: List<SavedPersonDataModel> = emptyList(),
    val selectedTab: LibraryTab = LibraryTab.Movies,
    val isTmdbAccountConnected: Boolean = false,
    val removingMovieIds: Set<Int> = emptySet(),
    val removingPersonIds: Set<Int> = emptySet(),
    val updatingTmdbMovieIds: Set<Int> = emptySet(),
    val tmdbActivityTimes: Map<TmdbLibraryList, Map<Int, Long>> = emptyMap(),
)

val FavoritesUiState.selectedItemCount: Int
    get() = when (selectedTab) {
        LibraryTab.Movies -> movies.size
        LibraryTab.People -> people.size
        LibraryTab.Tmdb -> 0
        LibraryTab.Rated -> 0
    }

val FavoritesUiState.hasSelectedTabItems: Boolean
    get() = selectedTab == LibraryTab.Tmdb || selectedTab == LibraryTab.Rated || selectedItemCount > 0

val FavoritesUiState.librarySubtitle: String
    get() = "Your saved movies, people & TMDB activity"
