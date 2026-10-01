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
import com.norbertotaveras.reels.domain.usecase.movie.GetDiscoverMovieCountUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieGenresUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.UpdateDiscoverFiltersUseCase
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val getDiscoverMovieCountUseCase: GetDiscoverMovieCountUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateDiscoverFiltersUseCase: UpdateDiscoverFiltersUseCase,
) : ViewModel() {
    private var filterResultCountJob: Job? = null

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
    }

    fun onEvent(event: DiscoverEvent) {
        when (event) {
            DiscoverEvent.FiltersClicked -> showFilters()
            DiscoverEvent.FiltersDismissed -> hideFilters()
            DiscoverEvent.ApplyFiltersClicked -> applyFilters()
            DiscoverEvent.ResetDraftFiltersClicked -> resetDraftFilters()
            is DiscoverEvent.SortSelected -> updateSort(sort = event.sort)
            is DiscoverEvent.GenreSelected -> updateGenre(genreId = event.genreId)
            is DiscoverEvent.ReleasePeriodSelected -> updateReleasePeriod(
                yearFrom = event.yearFrom,
                yearTo = event.yearTo
            )
            is DiscoverEvent.RatingSelected -> updateRating(minimumRating = event.minimumRating)
            DiscoverEvent.ClearFiltersClicked -> clearFilters()
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
        loadFilterResultCount()
    }

    private fun hideFilters() {
        filterResultCountJob?.cancel()
        _state.update { currentState ->
            currentState.copy(
                draftFilters = null,
                isFilterSheetVisible = false,
                filterResultCount = null,
                isLoadingFilterResultCount = false
            )
        }
    }

    private fun applyFilters() {
        filterResultCountJob?.cancel()
        val filters = state.value.draftFilters ?: state.value.filters
        _state.update { currentState ->
            currentState.copy(
                filters = filters,
                draftFilters = null,
                isFilterSheetVisible = false,
                filterResultCount = null,
                isLoadingFilterResultCount = false
            )
        }
        persistFilters(filters = filters)
    }

    private fun resetDraftFilters() {
        val filters = DiscoverFilterDataModel()
        _state.update { currentState ->
            currentState.copy(draftFilters = filters)
        }
        loadFilterResultCount(filters = filters)
    }

    private fun observeSettings() {
        viewModelScope.launch {
            var loadedGenreLanguage: String? = null

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

                    if (loadedGenreLanguage != language) {
                        loadedGenreLanguage = language
                        loadGenres(language = language)
                    }

                    if (state.value.isFilterSheetVisible) {
                        loadFilterResultCount()
                    }
                }
        }
    }

    private fun loadGenres(language: String = state.value.language) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoadingFilters = true,
                    filterErrorMessage = null
                )
            }

            getMovieGenresUseCase(language = language)
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

    private fun updateGenre(genreId: Int?) {
        updateDraftFilters { filters -> filters.copy(genreId = genreId) }
    }

    private fun updateReleasePeriod(
        yearFrom: Int?,
        yearTo: Int?,
    ) {
        updateDraftFilters { filters ->
            filters.copy(
                releaseYearFrom = yearFrom,
                releaseYearTo = yearTo
            )
        }
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

        var updatedFilters = state.value.filterSheetFilters
        _state.update { currentState ->
            updatedFilters = transform(currentState.draftFilters ?: currentState.filters)
            currentState.copy(
                draftFilters = updatedFilters
            )
        }
        loadFilterResultCount(filters = updatedFilters)
    }

    private fun clearFilters() {
        if (state.value.isFilterSheetVisible) {
            resetDraftFilters()
        } else {
            updateFilters(filters = DiscoverFilterDataModel())
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

    private fun loadFilterResultCount(
        filters: DiscoverFilterDataModel = state.value.filterSheetFilters,
    ) {
        val language = state.value.language
        filterResultCountJob?.cancel()
        _state.update { currentState ->
            currentState.copy(isLoadingFilterResultCount = true)
        }

        filterResultCountJob = viewModelScope.launch {
            delay(FILTER_COUNT_DEBOUNCE_MILLIS)
            getDiscoverMovieCountUseCase(
                filters = filters,
                language = language
            ).fold(
                onSuccess = { count ->
                    _state.update { currentState ->
                        if (
                            currentState.isFilterSheetVisible &&
                            currentState.filterSheetFilters == filters &&
                            currentState.language == language
                        ) {
                            currentState.copy(
                                filterResultCount = count,
                                isLoadingFilterResultCount = false
                            )
                        } else {
                            currentState
                        }
                    }
                },
                onFailure = {
                    _state.update { currentState ->
                        if (
                            currentState.isFilterSheetVisible &&
                            currentState.filterSheetFilters == filters &&
                            currentState.language == language
                        ) {
                            currentState.copy(
                                filterResultCount = null,
                                isLoadingFilterResultCount = false
                            )
                        } else {
                            currentState
                        }
                    }
                }
            )
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(DiscoverEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }

    private companion object {
        const val FILTER_COUNT_DEBOUNCE_MILLIS = 250L
    }
}
