/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.norbertotaveras.reels.domain.model.discover.DiscoverFilterDataModel
import com.norbertotaveras.reels.domain.model.discover.DiscoverSort
import com.norbertotaveras.reels.domain.usecase.movie.DiscoverMoviesUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieGenresUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateDiscoverFiltersUseCase
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModel @Inject constructor(
    private val discoverMoviesUseCase: DiscoverMoviesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateDiscoverFiltersUseCase: UpdateDiscoverFiltersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DiscoverUiState())
    val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DiscoverEffect>()
    val effect: SharedFlow<DiscoverEffect> = _effect.asSharedFlow()

    val movies = state
        .map { uiState -> uiState.filters to uiState.language }
        .distinctUntilChanged()
        .flatMapLatest { (filters, language) ->
            discoverMoviesUseCase(
                filters = filters,
                language = language
            )
        }
        .cachedIn(viewModelScope)

    init {
        observeSettings()
        loadGenres()
    }

    fun onEvent(event: DiscoverEvent) {
        when (event) {
            DiscoverEvent.FiltersClicked -> showFilters()
            DiscoverEvent.FiltersDismissed -> hideFilters()
            DiscoverEvent.ApplyFiltersClicked -> applyFilters()
            DiscoverEvent.ResetDraftFiltersClicked -> resetDraftFilters()
            is DiscoverEvent.SortSelected -> updateSort(sort = event.sort)
            is DiscoverEvent.GenreSelected -> updateGenre(
                genreId = event.genreId,
                genreName = event.genreName
            )
            is DiscoverEvent.YearSelected -> updateYear(year = event.year)
            is DiscoverEvent.RatingSelected -> updateRating(minimumRating = event.minimumRating)
            DiscoverEvent.ClearFiltersClicked -> updateFilters(filters = DiscoverFilterDataModel())
            DiscoverEvent.RetryFiltersClicked -> loadGenres()
            is DiscoverEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    private fun showFilters() {
        _state.update { currentState ->
            currentState.copy(
                draftFilters = currentState.filters,
                isFilterSheetVisible = true
            )
        }
    }

    private fun hideFilters() {
        _state.update { currentState ->
            currentState.copy(
                draftFilters = null,
                isFilterSheetVisible = false
            )
        }
    }

    private fun applyFilters() {
        val filters = state.value.draftFilters ?: state.value.filters
        _state.update { currentState ->
            currentState.copy(
                filters = filters,
                draftFilters = null,
                isFilterSheetVisible = false
            )
        }
        persistFilters(filters = filters)
    }

    private fun resetDraftFilters() {
        _state.update { currentState ->
            currentState.copy(draftFilters = DiscoverFilterDataModel())
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase()
                .map { settings -> settings.languageRegion.languageCode to settings.discoverFilters }
                .distinctUntilChanged()
                .collect { (language, filters) ->
                    _state.update { currentState ->
                        currentState.copy(
                            language = language,
                            filters = filters
                        )
                    }
                }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoadingFilters = true,
                    filterErrorMessage = null
                )
            }

            getMovieGenresUseCase(language = state.value.language)
                .onSuccess { genres ->
                    _state.update { currentState ->
                        currentState.copy(
                            genres = genres,
                            isLoadingFilters = false,
                            filterErrorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoadingFilters = false,
                            filterErrorMessage = error.toUserMessage(
                                fallback = "Unable to load discover filters."
                            )
                        )
                    }
                }
        }
    }

    private fun updateSort(sort: DiscoverSort) {
        updateDraftFilters { filters -> filters.copy(sort = sort) }
    }

    private fun updateGenre(genreId: Int?, genreName: String?) {
        updateDraftFilters { filters ->
            filters.copy(
                genreId = genreId,
                genreName = genreName
            )
        }
    }

    private fun updateYear(year: Int?) {
        updateDraftFilters { filters -> filters.copy(year = year) }
    }

    private fun updateRating(minimumRating: Int?) {
        updateDraftFilters { filters -> filters.copy(minimumRating = minimumRating) }
    }

    private fun updateDraftFilters(
        transform: (DiscoverFilterDataModel) -> DiscoverFilterDataModel,
    ) {
        if (!state.value.isFilterSheetVisible) {
            updateFilters(filters = transform(state.value.filters))
            return
        }

        _state.update { currentState ->
            currentState.copy(
                draftFilters = transform(currentState.draftFilters ?: currentState.filters)
            )
        }
    }

    private fun updateFilters(filters: DiscoverFilterDataModel) {
        _state.update { currentState ->
            currentState.copy(filters = filters)
        }

        persistFilters(filters = filters)
    }

    private fun persistFilters(filters: DiscoverFilterDataModel) {
        viewModelScope.launch {
            updateDiscoverFiltersUseCase(filters = filters)
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(DiscoverEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }
}
