/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.library.usecase.ObserveIsPersonSavedUseCase
import com.norbertotaveras.reels.domain.library.usecase.ToggleSavedPersonUseCase
import com.norbertotaveras.reels.domain.network.usecase.ObserveNetworkConnectivityUseCase
import com.norbertotaveras.reels.domain.person.usecase.LoadPersonDetailsContentUseCase
import com.norbertotaveras.reels.domain.settings.model.LanguageRegion
import com.norbertotaveras.reels.domain.settings.usecase.ObserveSettingsUseCase
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
class CastDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadPersonDetailsContentUseCase: LoadPersonDetailsContentUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val observeIsPersonSavedUseCase: ObserveIsPersonSavedUseCase,
    private val toggleSavedPersonUseCase: ToggleSavedPersonUseCase,
    private val observeNetworkConnectivityUseCase: ObserveNetworkConnectivityUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CastDetailsUiState())
    val state: StateFlow<CastDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CastDetailsEffect>()
    val effect: SharedFlow<CastDetailsEffect> = _effect.asSharedFlow()

    private val personId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.PERSON_ID_ARG)
    )
    private var currentLanguage: String = LanguageRegion.Default.languageCode
    private var loadPersonJob: Job? = null

    init {
        observeConnectivity()
        observeSettings()
        observeSavedStatus()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            observeNetworkConnectivityUseCase().collect { isOnline ->
                _state.update { currentState ->
                    currentState.copy(
                        isOnline = isOnline,
                        isOfflineSavedPerson = currentState.isSaved && !isOnline
                    )
                }
            }
        }
    }

    fun onEvent(event: CastDetailsEvent) {
        when (event) {
            CastDetailsEvent.BackClicked -> onBackClicked()
            CastDetailsEvent.RetryClicked -> loadPerson(language = currentLanguage)
            CastDetailsEvent.FavoriteClicked -> onFavoriteClicked()
            CastDetailsEvent.FilmographyClicked -> onFilmographyClicked()
            is CastDetailsEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    private fun loadPerson(language: String = currentLanguage) {
        loadPersonJob?.cancel()
        loadPersonJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    isOfflineSavedPerson = false,
                    errorMessage = null
                )
            }

            loadPersonDetailsContentUseCase(
                personId = personId,
                language = language
            ).fold(
                onSuccess = { content ->
                    _state.update { currentState ->
                        currentState.withContent(content = content)
                    }
                },
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            errorMessage = error.toUserMessage(
                                fallback = "Unable to load cast details."
                            ),
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    private fun observeSavedStatus() {
        viewModelScope.launch {
            observeIsPersonSavedUseCase(personId = personId).collect { isSaved ->
                _state.update { currentState ->
                    currentState.copy(
                        isSaved = isSaved,
                        isOfflineSavedPerson = isSaved && !currentState.isOnline
                    )
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase()
                .map { settings -> settings.languageRegion.languageCode }
                .distinctUntilChanged()
                .collect { language ->
                    currentLanguage = language
                    loadPerson(language = language)
                }
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _effect.emit(CastDetailsEffect.NavigateBack)
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(CastDetailsEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }

    private fun onFilmographyClicked() {
        viewModelScope.launch {
            _effect.emit(CastDetailsEffect.NavigateToFilmography(personId = personId))
        }
    }

    private fun onFavoriteClicked() {
        viewModelScope.launch {
            val person = _state.value.person
            if (person == null || _state.value.isSaving) return@launch

            _state.update { currentState ->
                currentState.copy(isSaving = true)
            }

            try {
                toggleSavedPersonUseCase(
                    isSaved = _state.value.isSaved,
                    person = person,
                    knownFor = _state.value.knownFor
                ).onSuccess { isSaved ->
                    _effect.emit(
                        CastDetailsEffect.ShowMessage(
                            message = if (isSaved) {
                                "Saved offline"
                            } else {
                                "Removed from library"
                            }
                        )
                    )
                }.onFailure { error ->
                    _effect.emit(
                        CastDetailsEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Unable to update library."
                            )
                        )
                    )
                }
            } finally {
                _state.update { currentState ->
                    currentState.copy(isSaving = false)
                }
            }
        }
    }
}
