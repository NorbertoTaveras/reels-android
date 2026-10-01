/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.model.person.FilmographySort
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.domain.usecase.person.LoadFilmographyContentUseCase
import com.norbertotaveras.reels.domain.usecase.person.SortPersonMovieCreditsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FilmographyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadFilmographyContentUseCase: LoadFilmographyContentUseCase,
    private val sortPersonMovieCreditsUseCase: SortPersonMovieCreditsUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    private val personId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.PERSON_ID_ARG)
    )

    private val _state = MutableStateFlow(FilmographyUiState())
    val state: StateFlow<FilmographyUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FilmographyEffect>()
    val effect: SharedFlow<FilmographyEffect> = _effect.asSharedFlow()

    private var currentLanguage: String = LanguageRegion.Default.languageCode
    private var loadFilmographyJob: Job? = null

    init {
        observeSettings()
    }

    fun onEvent(event: FilmographyEvent) {
        when (event) {
            FilmographyEvent.BackClicked -> onBackClicked()
            FilmographyEvent.RetryClicked -> loadFilmography(language = currentLanguage)
            is FilmographyEvent.SortSelected -> onSortSelected(sort = event.sort)
            is FilmographyEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase()
                .map { settings -> settings.languageRegion.languageCode }
                .distinctUntilChanged()
                .collect { language ->
                    currentLanguage = language
                    loadFilmography(language = language)
                }
        }
    }

    private fun loadFilmography(language: String) {
        loadFilmographyJob?.cancel()
        loadFilmographyJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            loadFilmographyContentUseCase(
                personId = personId,
                language = language
            ).fold(
                onSuccess = { content ->
                    _state.update { currentState ->
                        currentState.withContent(
                            content = content.copy(
                                movies = sortPersonMovieCreditsUseCase(
                                    credits = content.movies,
                                    sort = currentState.sort
                                )
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.toUserMessage(
                                fallback = "Unable to load filmography."
                            )
                        )
                    }
                }
            )
        }
    }

    private fun onSortSelected(sort: FilmographySort) {
        _state.update { currentState ->
            currentState.copy(
                sort = sort,
                movies = sortPersonMovieCreditsUseCase(
                    credits = currentState.movies,
                    sort = sort
                )
            )
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _effect.emit(FilmographyEffect.NavigateBack)
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(FilmographyEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }
}
