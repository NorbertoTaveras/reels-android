/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieGenresUseCase
import com.norbertotaveras.reels.domain.usecase.movie.SearchMoviesUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateRecentSearchesUseCase
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
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateRecentSearchesUseCase: UpdateRecentSearchesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SearchEffect>()
    val effect: SharedFlow<SearchEffect> = _effect.asSharedFlow()

    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<MovieDataModel>> = state
        .map { uiState -> uiState.query.trim() to uiState.language }
        .debounce(400)
        .distinctUntilChanged()
        .flatMapLatest { (query, language) ->
            if (query.length < MIN_QUERY_LENGTH) {
                flowOf(PagingData.empty())
            } else {
                searchMoviesUseCase(
                    query = query,
                    language = language
                )
            }
        }
        .cachedIn(viewModelScope)

    init {
        observeSettings()
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
        saveCurrentQueryAsRecentSearch()
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

        viewModelScope.launch {
            updateRecentSearchesUseCase(recentSearches = emptyList())
        }
    }

    private fun onMovieClicked(movieId: Int) {
        saveCurrentQueryAsRecentSearch()

        viewModelScope.launch {
            _effect.emit(SearchEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }

    private fun saveCurrentQueryAsRecentSearch() {
        val currentState = state.value
        val query = currentState.query.trim()

        if (query.length < MIN_QUERY_LENGTH) return

        val recentSearches = buildRecentSearches(
            query = query,
            currentRecentSearches = currentState.recentSearches
        )

        _state.update { currentState ->
            currentState.copy(recentSearches = recentSearches)
        }

        viewModelScope.launch {
            updateRecentSearchesUseCase(recentSearches = recentSearches)
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            var loadedGenreLanguage: String? = null

            observeSettingsUseCase()
                .collect { settings ->
                    val language = settings.languageRegion.languageCode

                    _state.update { currentState ->
                        currentState.copy(
                            language = language,
                            recentSearches = settings.recentSearches
                        )
                    }

                    if (loadedGenreLanguage != language) {
                        loadedGenreLanguage = language
                        loadGenres(language = language)
                    }
                }
        }
    }

    private fun buildRecentSearches(
        query: String,
        currentRecentSearches: List<String>,
    ): List<String> {
        return buildList {
            add(query)
            addAll(
                currentRecentSearches.filterNot { recent ->
                    recent.equals(query, ignoreCase = true)
                }
            )
        }.take(MAX_RECENT_SEARCHES)
    }

    private fun loadGenres(language: String) {
        viewModelScope.launch {
            getMovieGenresUseCase(language = language).fold(
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
