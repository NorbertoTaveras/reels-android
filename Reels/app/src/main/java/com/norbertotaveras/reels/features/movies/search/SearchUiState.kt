package com.norbertotaveras.reels.features.movies.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SearchUiState {
    var query by mutableStateOf("")
    var recentSearches by mutableStateOf<List<String>>(emptyList())
    var genreNamesById by mutableStateOf<Map<Int, String>>(emptyMap())
}
