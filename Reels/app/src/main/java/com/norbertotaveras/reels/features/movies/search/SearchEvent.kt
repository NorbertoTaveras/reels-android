package com.norbertotaveras.reels.features.movies.search

sealed interface SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent
    data object ClearQueryClicked : SearchEvent
    data object SearchSubmitted : SearchEvent
    data class RecentSearchClicked(val query: String) : SearchEvent
    data object ClearRecentSearchesClicked : SearchEvent
    data class MovieClicked(val movieId: Int) : SearchEvent
}
