/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.norbertotaveras.reels.domain.model.constant.MovieCategory
import com.norbertotaveras.reels.domain.usecase.movie.GetMoviesUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MoviesListViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MoviesListState())
    val state: StateFlow<MoviesListState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MoviesListEffect>()
    val effect: SharedFlow<MoviesListEffect> = _effect.asSharedFlow()

    val popularMovies = moviesForCategory(MovieCategory.POPULAR)
    val nowPlayingMovies = moviesForCategory(MovieCategory.NOW_PLAYING)
    val upcomingMovies = moviesForCategory(MovieCategory.UPCOMING)
    val topRatedMovies = moviesForCategory(MovieCategory.TOP_RATED)

    private fun moviesForCategory(category: MovieCategory) = state
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

    fun onEvent(event: MoviesListEvent) {
        when (event) {
            is MoviesListEvent.SeeAllClicked -> onSeeAllClicked(category = event.category)
            is MoviesListEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
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

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(MoviesListEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }

    private fun onSeeAllClicked(category: MovieCategory) {
        viewModelScope.launch {
            _effect.emit(MoviesListEffect.NavigateToMovieCategory(categoryValue = category.value))
        }
    }
}
