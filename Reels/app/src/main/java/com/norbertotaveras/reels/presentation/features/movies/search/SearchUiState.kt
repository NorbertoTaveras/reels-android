package com.norbertotaveras.reels.presentation.features.movies.search

data class SearchUiState(
    val query: String = "",
    val language: String = "en-US",
    val recentSearches: List<String> = emptyList(),
    val genreNamesById: Map<Int, String> = emptyMap()
)
