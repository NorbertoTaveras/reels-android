/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.model.favorite.SavedPersonDataModel
import com.norbertotaveras.reels.domain.model.person.PersonDetailsDataModel
import com.norbertotaveras.reels.domain.usecase.favorites.GetSavedPersonDetailsUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveIsPersonSavedUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ToggleSavedPersonUseCase
import com.norbertotaveras.reels.domain.usecase.person.GetPersonDetailsUseCase
import com.norbertotaveras.reels.domain.usecase.person.GetPersonMovieCreditsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
class CastDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPersonDetailsUseCase: GetPersonDetailsUseCase,
    private val getPersonMovieCreditsUseCase: GetPersonMovieCreditsUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val observeIsPersonSavedUseCase: ObserveIsPersonSavedUseCase,
    private val getSavedPersonDetailsUseCase: GetSavedPersonDetailsUseCase,
    private val toggleSavedPersonUseCase: ToggleSavedPersonUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CastDetailsUiState())
    val state: StateFlow<CastDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CastDetailsEffect>()
    val effect: SharedFlow<CastDetailsEffect> = _effect.asSharedFlow()

    private val personId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.PERSON_ID_ARG)
    )
    private var currentLanguage: String = DEFAULT_LANGUAGE
    private var loadPersonJob: Job? = null

    init {
        observeSettings()
        observeSavedStatus()
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

    fun loadPerson(language: String = currentLanguage) {
        loadPersonJob?.cancel()
        loadPersonJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    isOfflineSavedPerson = false,
                    errorMessage = null
                )
            }

            val personResultDeferred = async {
                getPersonDetailsUseCase(
                    personId = personId,
                    language = language
                )
            }

            val creditsResultDeferred = async {
                getPersonMovieCreditsUseCase(
                    personId = personId,
                    language = language
                )
            }

            val personResult = personResultDeferred.await()
            val creditsResult = creditsResultDeferred.await()

            val savedPerson = if (personResult.isFailure) {
                getSavedPersonDetailsUseCase(personId = personId)
            } else {
                null
            }

            _state.update { currentState ->
                personResult.fold(
                    onSuccess = { person ->
                        currentState.copy(
                            person = person,
                            knownFor = creditsResult
                                .getOrNull()
                                ?.cast
                                .orEmpty(),
                            isOfflineSavedPerson = false,
                            errorMessage = null,
                            isLoading = false
                        )
                    },
                    onFailure = { error ->
                        if (savedPerson != null) {
                            currentState.copy(
                                person = savedPerson.toPersonDetails(),
                                knownFor = savedPerson.knownFor,
                                isOfflineSavedPerson = true,
                                errorMessage = null,
                                isLoading = false
                            )
                        } else {
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
    }

    private fun observeSavedStatus() {
        viewModelScope.launch {
            observeIsPersonSavedUseCase(personId = personId).collect { isSaved ->
                _state.update { currentState ->
                    currentState.copy(isSaved = isSaved)
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
                                "Saved to library."
                            } else {
                                "Removed from library."
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

    private companion object {
        const val DEFAULT_LANGUAGE = "en-US"
    }
}

private fun SavedPersonDataModel.toPersonDetails(): PersonDetailsDataModel {
    return PersonDetailsDataModel(
        id = id,
        name = name,
        biography = biography,
        birthday = birthday,
        placeOfBirth = placeOfBirth,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment
    )
}
