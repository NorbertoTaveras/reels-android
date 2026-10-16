/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.cast

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveSavedPeopleUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ToggleSavedCastMemberUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieCreditsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class MovieCastViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
    private val observeSavedPeopleUseCase: ObserveSavedPeopleUseCase,
    private val toggleSavedCastMemberUseCase: ToggleSavedCastMemberUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    private val movieId: Int = checkNotNull(
        savedStateHandle[ReelsDestinations.MOVIE_ID_ARG]
    )

    private val _state = MutableStateFlow(MovieCastUiState())
    val state: StateFlow<MovieCastUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MovieCastEffect>()
    val effect: SharedFlow<MovieCastEffect> = _effect.asSharedFlow()

    private var currentLanguage: String = LanguageRegion.Default.languageCode
    private var loadCastJob: Job? = null

    init {
        observeSettings()
        observeSavedPeople()
    }

    fun onEvent(event: MovieCastEvent) {
        when (event) {
            MovieCastEvent.BackClicked -> onBackClicked()
            MovieCastEvent.RetryClicked -> loadCast(language = currentLanguage)
            is MovieCastEvent.CastClicked -> onCastClicked(personId = event.personId)
            is MovieCastEvent.SaveClicked -> onSaveClicked(personId = event.personId)
        }
    }

    private fun observeSavedPeople() {
        viewModelScope.launch {
            observeSavedPeopleUseCase().collect { savedPeople ->
                _state.update { currentState ->
                    currentState.copy(
                        savedPersonIds = savedPeople.map { person -> person.id }.toSet()
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
                    loadCast(language = language)
                }
        }
    }

    private fun loadCast(language: String) {
        loadCastJob?.cancel()
        loadCastJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            getMovieCreditsUseCase(
                movieId = movieId,
                language = language
            ).fold(
                onSuccess = { credits ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            cast = credits.cast,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.toUserMessage(
                                fallback = "Unable to load full cast."
                            )
                        )
                    }
                }
            )
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _effect.emit(MovieCastEffect.NavigateBack)
        }
    }

    private fun onCastClicked(personId: Int) {
        viewModelScope.launch {
            _effect.emit(MovieCastEffect.NavigateToCastDetails(personId = personId))
        }
    }

    private fun onSaveClicked(personId: Int) {
        viewModelScope.launch {
            val currentState = _state.value
            if (personId in currentState.savingPersonIds) return@launch

            val isSaved = personId in currentState.savedPersonIds
            _state.update { state ->
                state.copy(savingPersonIds = state.savingPersonIds + personId)
            }

            try {
                toggleSavedCastMemberUseCase(
                    personId = personId,
                    isSaved = isSaved,
                    language = currentLanguage
                ).fold(
                    onSuccess = { saved ->
                        _effect.emit(
                            MovieCastEffect.ShowMessage(
                                message = if (saved) {
                                    "Saved offline"
                                } else {
                                    "Removed from library"
                                }
                            )
                        )
                    },
                    onFailure = { error ->
                        _effect.emit(
                            MovieCastEffect.ShowMessage(
                                message = error.toUserMessage(
                                    fallback = "Unable to update library."
                                )
                            )
                        )
                    }
                )
            } finally {
                _state.update { state ->
                    state.copy(savingPersonIds = state.savingPersonIds - personId)
                }
            }
        }
    }

}
