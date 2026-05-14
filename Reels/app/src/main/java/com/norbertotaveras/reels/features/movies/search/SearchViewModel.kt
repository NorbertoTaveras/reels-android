package com.norbertotaveras.reels.features.movies.search

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.norbertotaveras.reels.model.data.movie.MovieDataModel
import com.norbertotaveras.reels.usecases.movie.GetMovieGenresUseCase
import com.norbertotaveras.reels.usecases.movie.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
) : ViewModel() {
    val state = SearchUiState()

    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<MovieDataModel>> = snapshotFlow { state.query }
        .map { query -> query.trim() }
        .debounce(400)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.length < MIN_QUERY_LENGTH) {
                flowOf(PagingData.empty())
            } else {
                searchMoviesUseCase(query = query)
            }
        }
        .cachedIn(viewModelScope)

    init {
        loadGenres()
    }

    fun onQueryChanged(query: String) {
        state.query = query
    }

    fun onClearQuery() {
        state.query = ""
    }

    fun onSearchSubmitted() {
        val query = state.query.trim()

        if (query.length < MIN_QUERY_LENGTH) return

        state.recentSearches = buildList {
            add(query)
            addAll(state.recentSearches.filterNot { recent ->
                recent.equals(query, ignoreCase = true)
            })
        }.take(MAX_RECENT_SEARCHES)
    }

    fun onRecentSearchClicked(query: String) {
        state.query = query
    }

    fun onClearRecentSearches() {
        state.recentSearches = emptyList()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            getMovieGenresUseCase().fold(
                onSuccess = { genres ->
                    state.genreNamesById = genres.associate { genre ->
                        genre.id to genre.name
                    }
                },
                onFailure = {
                    state.genreNamesById = emptyMap()
                }
            )
        }
    }

    private companion object {
        const val MIN_QUERY_LENGTH = 2
        const val MAX_RECENT_SEARCHES = 5
    }
}
