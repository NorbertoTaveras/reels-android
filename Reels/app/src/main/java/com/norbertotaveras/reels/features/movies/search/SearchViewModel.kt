package com.norbertotaveras.reels.features.movies.search

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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SearchEffect>()
    val effect: SharedFlow<SearchEffect> = _effect.asSharedFlow()

    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<MovieDataModel>> = state
        .map { uiState -> uiState.query.trim() }
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

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> onQueryChanged(event.query)
            SearchEvent.ClearQueryClicked -> onClearQuery()
            SearchEvent.SearchSubmitted -> onSearchSubmitted()
            is SearchEvent.RecentSearchClicked -> onRecentSearchClicked(event.query)
            SearchEvent.ClearRecentSearchesClicked -> onClearRecentSearches()
            is SearchEvent.MovieClicked -> onMovieClicked(event.movieId)
        }
    }

    private fun onQueryChanged(query: String) {
        _state.update { currentState ->
            currentState.copy(query = query)
        }
    }

    private fun onClearQuery() {
        _state.update { currentState ->
            currentState.copy(query = "")
        }
    }

    private fun onSearchSubmitted() {
        val currentState = state.value
        val query = currentState.query.trim()

        if (query.length < MIN_QUERY_LENGTH) return

        val recentSearches = buildList {
            add(query)
            addAll(currentState.recentSearches.filterNot { recent ->
                recent.equals(query, ignoreCase = true)
            })
        }.take(MAX_RECENT_SEARCHES)

        _state.update {
            it.copy(recentSearches = recentSearches)
        }
    }

    private fun onRecentSearchClicked(query: String) {
        _state.update { currentState ->
            currentState.copy(query = query)
        }
    }

    private fun onClearRecentSearches() {
        _state.update { currentState ->
            currentState.copy(recentSearches = emptyList())
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(SearchEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            getMovieGenresUseCase().fold(
                onSuccess = { genres ->
                    _state.update { currentState ->
                        currentState.copy(
                            genreNamesById = genres.associate { genre ->
                                genre.id to genre.name
                            }
                        )
                    }
                },
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(genreNamesById = emptyMap())
                    }
                }
            )
        }
    }

    private companion object {
        const val MIN_QUERY_LENGTH = 2
        const val MAX_RECENT_SEARCHES = 5
    }
}
