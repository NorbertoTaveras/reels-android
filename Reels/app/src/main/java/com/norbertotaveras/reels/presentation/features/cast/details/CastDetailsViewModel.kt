package com.norbertotaveras.reels.presentation.features.cast.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.usecase.person.GetPersonDetailsUseCase
import com.norbertotaveras.reels.domain.usecase.person.GetPersonMovieCreditsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {
    private val _state = MutableStateFlow(CastDetailsUiState())
    val state: StateFlow<CastDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CastDetailsEffect>()
    val effect: SharedFlow<CastDetailsEffect> = _effect.asSharedFlow()

    private val personId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.PERSON_ID_ARG)
    )
    private var currentLanguage: String = DEFAULT_LANGUAGE

    init {
        observeSettings()
    }

    fun onEvent(event: CastDetailsEvent) {
        when (event) {
            CastDetailsEvent.BackClicked -> onBackClicked()
            CastDetailsEvent.RetryClicked -> loadPerson(language = currentLanguage)
            CastDetailsEvent.FavoriteClicked -> onFavoriteClicked()
            is CastDetailsEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    fun loadPerson(language: String = currentLanguage) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
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

            personResult.fold(
                onSuccess = { person ->
                    _state.update { currentState ->
                        currentState.copy(person = person)
                    }
                },
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            errorMessage = error.localizedMessage ?: "Unable to load cast details."
                        )
                    }
                }
            )

            creditsResult.fold(
                onSuccess = { credits ->
                    _state.update { currentState ->
                        currentState.copy(knownFor = credits.cast.take(10))
                    }
                },
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(knownFor = emptyList())
                    }
                }
            )

            _state.update { currentState ->
                currentState.copy(isLoading = false)
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

    private fun onFavoriteClicked() {
        viewModelScope.launch {
            _effect.emit(
                CastDetailsEffect.ShowMessage(
                    message = "Person favorites are not available in this version."
                )
            )
        }
    }

    private companion object {
        const val DEFAULT_LANGUAGE = "en-US"
    }
}
