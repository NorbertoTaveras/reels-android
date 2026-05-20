package com.norbertotaveras.reels.features.movies.search

data class SearchUiState(
    val query: String = "",
    val recentSearches: List<String> = emptyList(),
    val genreNamesById: Map<Int, String> = emptyMap()
)
