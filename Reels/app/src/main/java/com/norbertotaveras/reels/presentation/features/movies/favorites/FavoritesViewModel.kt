/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveFavoriteMoviesUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveSavedPeopleUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.RemoveFavoriteMovieUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.RemoveSavedPersonUseCase
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase,
    private val observeSavedPeopleUseCase: ObserveSavedPeopleUseCase,
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase,
    private val removeSavedPersonUseCase: RemoveSavedPersonUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FavoritesEffect>()
    val effect: SharedFlow<FavoritesEffect> = _effect.asSharedFlow()

    init {
        observeFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.TabSelected -> onTabSelected(tab = event.tab)
            is FavoritesEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
            is FavoritesEvent.PersonClicked -> onPersonClicked(personId = event.personId)
            is FavoritesEvent.FavoriteClicked -> onFavoriteClicked(movieId = event.movieId)
            is FavoritesEvent.SavedPersonClicked -> onSavedPersonClicked(personId = event.personId)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteMoviesUseCase().collectLatest { movies ->
                _state.update { currentState ->
                    currentState.copy(movies = movies)
                }
            }
        }

        viewModelScope.launch {
            observeSavedPeopleUseCase().collectLatest { people ->
                _state.update { currentState ->
                    currentState.copy(people = people)
                }
            }
        }
    }

    private fun onTabSelected(tab: LibraryTab) {
        _state.update { currentState ->
            currentState.copy(selectedTab = tab)
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(FavoritesEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }

    private fun onPersonClicked(personId: Int) {
        viewModelScope.launch {
            _effect.emit(FavoritesEffect.NavigateToCastDetails(personId = personId))
        }
    }

    private fun onFavoriteClicked(movieId: Int) {
        viewModelScope.launch {
            if (_state.value.removingMovieIds.contains(movieId)) return@launch

            _state.update { currentState ->
                currentState.copy(
                    removingMovieIds = currentState.removingMovieIds + movieId
                )
            }

            try {
                removeFavoriteMovieUseCase(movieId = movieId).onSuccess {
                    _effect.emit(FavoritesEffect.ShowMessage(message = "Removed from favorites."))
                }.onFailure { error ->
                    _effect.emit(
                        FavoritesEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Unable to remove favorite."
                            )
                        )
                    )
                }
            } finally {
                _state.update { currentState ->
                    currentState.copy(
                        removingMovieIds = currentState.removingMovieIds - movieId
                    )
                }
            }
        }
    }

    private fun onSavedPersonClicked(personId: Int) {
        viewModelScope.launch {
            if (_state.value.removingPersonIds.contains(personId)) return@launch

            _state.update { currentState ->
                currentState.copy(
                    removingPersonIds = currentState.removingPersonIds + personId
                )
            }

            try {
                removeSavedPersonUseCase(personId = personId).onSuccess {
                    _effect.emit(FavoritesEffect.ShowMessage(message = "Removed from library."))
                }.onFailure { error ->
                    _effect.emit(
                        FavoritesEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Unable to remove saved person."
                            )
                        )
                    )
                }
            } finally {
                _state.update { currentState ->
                    currentState.copy(
                        removingPersonIds = currentState.removingPersonIds - personId
                    )
                }
            }
        }
    }
}
