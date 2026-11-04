/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import com.norbertotaveras.reels.domain.movie.usecase.ObserveSearchCacheTimestampUseCase
import com.norbertotaveras.reels.domain.movie.usecase.SearchMoviesUseCase
import com.norbertotaveras.reels.domain.network.usecase.ObserveNetworkConnectivityUseCase
import com.norbertotaveras.reels.domain.search.model.SearchQueryPolicy
import com.norbertotaveras.reels.domain.search.usecase.ClearRecentSearchesUseCase
import com.norbertotaveras.reels.domain.search.usecase.LoadSearchMetadataUseCase
import com.norbertotaveras.reels.domain.search.usecase.RemoveRecentSearchUseCase
import com.norbertotaveras.reels.domain.search.usecase.SaveRecentSearchUseCase
import com.norbertotaveras.reels.domain.settings.usecase.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val observeSearchCacheTimestampUseCase: ObserveSearchCacheTimestampUseCase,
    private val loadSearchMetadataUseCase: LoadSearchMetadataUseCase,
    private val observeNetworkConnectivityUseCase: ObserveNetworkConnectivityUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val saveRecentSearchUseCase: SaveRecentSearchUseCase,
    private val removeRecentSearchUseCase: RemoveRecentSearchUseCase,
    private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SearchEffect>()
    val effect: SharedFlow<SearchEffect> = _effect.asSharedFlow()

    private val settings = observeSettingsUseCase()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<MovieDataModel>> = state
        .map { uiState -> uiState.query.trim() to uiState.language }
        .debounce(SEARCH_DEBOUNCE_MILLIS.milliseconds)
        .distinctUntilChanged()
        .flatMapLatest { (query, language) ->
            if (!SearchQueryPolicy.isValid(query = query)) {
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
        observeConnectivity()
        observeSettings()
        observeSearchMetadata()
        observeSearchCacheTimestamp()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            observeNetworkConnectivityUseCase().collectLatest { isOnline ->
                _state.update { currentState ->
                    currentState.copy(isOnline = isOnline)
                }
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> onQueryChanged(event.query)
            SearchEvent.ClearQueryClicked -> onClearQuery()
            SearchEvent.SearchSubmitted -> onSearchSubmitted()
            is SearchEvent.RecentSearchClicked -> onRecentSearchClicked(event.query)
            is SearchEvent.RecentSearchRemoved -> onRecentSearchRemoved(event.query)
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
        viewModelScope.launch {
            val recentSearches = clearRecentSearchesUseCase()
            _state.update { currentState ->
                currentState.copy(recentSearches = recentSearches)
            }
        }
    }

    private fun onRecentSearchRemoved(query: String) {
        val recentSearches = state.value.recentSearches
        viewModelScope.launch {
            val updatedSearches = removeRecentSearchUseCase(
                query = query,
                recentSearches = recentSearches
            )
            _state.update { currentState ->
                currentState.copy(recentSearches = updatedSearches)
            }
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
        if (!SearchQueryPolicy.isValid(query = currentState.query)) return

        viewModelScope.launch {
            val recentSearches = saveRecentSearchUseCase(
                query = currentState.query,
                recentSearches = currentState.recentSearches
            )
            _state.update { latestState ->
                latestState.copy(recentSearches = recentSearches)
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settings.collect { settings ->
                _state.update { currentState ->
                    currentState.withSettings(settings = settings)
                }
            }
        }
    }

    private fun observeSearchMetadata() {
        viewModelScope.launch {
            settings
                .map { appSettings -> appSettings.languageRegion.languageCode }
                .distinctUntilChanged()
                .collectLatest { language ->
                    val metadata = loadSearchMetadataUseCase(language = language)
                    _state.update { currentState ->
                        currentState.withMetadata(metadata = metadata)
                    }
                }
        }
    }

    private fun observeSearchCacheTimestamp() {
        viewModelScope.launch {
            state
                .map { uiState -> uiState.query.trim() to uiState.language }
                .distinctUntilChanged()
                .flatMapLatest { (query, language) ->
                    if (SearchQueryPolicy.isValid(query = query)) {
                        observeSearchCacheTimestampUseCase(
                            query = query,
                            language = language
                        )
                    } else {
                        flowOf(null)
                    }
                }
                .collectLatest { cachedAt ->
                    _state.update { currentState ->
                        currentState.copy(cachedAt = cachedAt)
                    }
                }
        }
    }
}
