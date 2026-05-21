package com.norbertotaveras.reels.features.cast.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.navigation.ReelsDestinations
import com.norbertotaveras.reels.usecases.person.GetPersonDetailsUseCase
import com.norbertotaveras.reels.usecases.person.GetPersonMovieCreditsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CastDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPersonDetailsUseCase: GetPersonDetailsUseCase,
    private val getPersonMovieCreditsUseCase: GetPersonMovieCreditsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CastDetailsUiState())
    val state: StateFlow<CastDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CastDetailsEffect>()
    val effect: SharedFlow<CastDetailsEffect> = _effect.asSharedFlow()

    private val personId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.PERSON_ID_ARG)
    )

    init {
        loadPerson()
    }

    fun onEvent(event: CastDetailsEvent) {
        when (event) {
            CastDetailsEvent.BackClicked -> onBackClicked()
            CastDetailsEvent.RetryClicked -> loadPerson()
            CastDetailsEvent.FavoriteClicked -> Unit
            is CastDetailsEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    fun loadPerson() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val personResultDeferred = async {
                getPersonDetailsUseCase(personId = personId)
            }

            val creditsResultDeferred = async {
                getPersonMovieCreditsUseCase(personId = personId)
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
}
