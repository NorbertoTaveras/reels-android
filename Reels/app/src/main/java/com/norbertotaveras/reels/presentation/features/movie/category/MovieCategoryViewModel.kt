/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.norbertotaveras.reels.domain.movie.model.MovieCategory
import com.norbertotaveras.reels.domain.movie.usecase.GetMoviesUseCase
import com.norbertotaveras.reels.domain.settings.usecase.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import com.norbertotaveras.reels.presentation.ui.extensions.toMovieCategory
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
class MovieCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getMoviesUseCase: GetMoviesUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    private val category = savedStateHandle
        .get<String>(ReelsDestinations.MOVIE_CATEGORY_ARG)
        .toMovieCategory()

    private val _state = MutableStateFlow(MovieCategoryUiState(category = category))
    val state: StateFlow<MovieCategoryUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MovieCategoryEffect>()
    val effect: SharedFlow<MovieCategoryEffect> = _effect.asSharedFlow()

    val movies = state
        .map { uiState -> uiState.language }
        .distinctUntilChanged()
        .flatMapLatest { language ->
            getMoviesUseCase(
                category = category,
                language = language
            )
        }
        .cachedIn(viewModelScope)

    init {
        observeSettings()
    }

    fun onEvent(event: MovieCategoryEvent) {
        when (event) {
            MovieCategoryEvent.BackClicked -> onBackClicked()
            is MovieCategoryEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase()
                .map { settings -> settings.languageRegion.languageCode }
                .distinctUntilChanged()
                .collect { language ->
                    _state.update { currentState ->
                        currentState.copy(language = language)
                    }
                }
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _effect.emit(MovieCategoryEffect.NavigateBack)
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(MovieCategoryEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }
}
