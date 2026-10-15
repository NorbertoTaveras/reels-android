/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.model.settings.LanguageRegion
import com.norbertotaveras.reels.domain.usecase.account.DeleteTmdbMovieRatingUseCase
import com.norbertotaveras.reels.domain.usecase.account.GetTmdbMovieAccountStateUseCase
import com.norbertotaveras.reels.domain.usecase.account.ObserveTmdbAccountUseCase
import com.norbertotaveras.reels.domain.usecase.account.RateTmdbMovieUseCase
import com.norbertotaveras.reels.domain.usecase.account.UpdateTmdbWatchlistUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveSavedPeopleUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveIsFavoriteUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ToggleSavedCastMemberUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ToggleFavoriteMovieUseCase
import com.norbertotaveras.reels.domain.usecase.movie.LoadMovieDetailsContentUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import com.norbertotaveras.reels.presentation.ui.extensions.toRatingText
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadMovieDetailsContentUseCase: LoadMovieDetailsContentUseCase,
    private val observeIsFavoriteUseCase: ObserveIsFavoriteUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
    private val observeSavedPeopleUseCase: ObserveSavedPeopleUseCase,
    private val toggleSavedCastMemberUseCase: ToggleSavedCastMemberUseCase,
    private val observeTmdbAccountUseCase: ObserveTmdbAccountUseCase,
    private val getTmdbMovieAccountStateUseCase: GetTmdbMovieAccountStateUseCase,
    private val updateTmdbWatchlistUseCase: UpdateTmdbWatchlistUseCase,
    private val rateTmdbMovieUseCase: RateTmdbMovieUseCase,
    private val deleteTmdbMovieRatingUseCase: DeleteTmdbMovieRatingUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailsUiState())
    val state: StateFlow<MovieDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MovieDetailsEffect>()
    val effect: SharedFlow<MovieDetailsEffect> = _effect.asSharedFlow()

    private val movieId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.MOVIE_ID_ARG)
    )
    private var currentLanguage: String = LanguageRegion.Default.languageCode
    private var loadMovieJob: Job? = null
    private var activeTmdbAccountId: Int? = null

    init {
        observeFavoriteState()
        observeSavedPeople()
        observeTmdbAccountState()
        observeSettings()
    }

    fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            MovieDetailsEvent.BackClicked -> onBackClicked()
            MovieDetailsEvent.RetryClicked -> loadMovie(language = currentLanguage)
            MovieDetailsEvent.AccountStateRetryClicked -> {
                viewModelScope.launch { refreshMovieAccountState() }
            }
            MovieDetailsEvent.FavoriteClicked -> onFavoriteClick()
            MovieDetailsEvent.WatchlistClicked -> onWatchlistClicked()
            MovieDetailsEvent.RatingClicked -> onRatingClicked()
            is MovieDetailsEvent.RatingSelected -> onRatingSelected(rating = event.rating)
            MovieDetailsEvent.RatingSheetDismissed -> onRatingSheetDismissed()
            MovieDetailsEvent.RatingSubmitted -> onRatingSubmitted()
            MovieDetailsEvent.RatingCleared -> onRatingCleared()
            is MovieDetailsEvent.CastClicked -> onCastClicked(personId = event.personId)
            is MovieDetailsEvent.CastSaveClicked -> onCastSaveClicked(personId = event.personId)
            MovieDetailsEvent.CastSeeAllClicked -> onCastSeeAllClicked()
            is MovieDetailsEvent.TrailerClicked -> onTrailerClicked(trailerId = event.trailerId)
            is MovieDetailsEvent.SimilarMovieClicked -> onSimilarMovieClicked(movieId = event.movieId)
        }
    }

    private fun loadMovie(language: String = currentLanguage) {
        loadMovieJob?.cancel()
        loadMovieJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            loadMovieDetailsContentUseCase(
                movieId = movieId,
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
                                fallback = "Unable to load movie details."
                            ),
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    private fun observeFavoriteState() {
        viewModelScope.launch {
            observeIsFavoriteUseCase(movieId = movieId).collectLatest { isFavorite ->
                _state.update { currentState ->
                    currentState.copy(isFavorite = isFavorite)
                }
            }
        }
    }

    private fun observeSavedPeople() {
        viewModelScope.launch {
            observeSavedPeopleUseCase().collectLatest { people ->
                _state.update { currentState ->
                    currentState.copy(savedPersonIds = people.map { person -> person.id }.toSet())
                }
            }
        }
    }

    private fun observeTmdbAccountState() {
        viewModelScope.launch {
            observeTmdbAccountUseCase().collectLatest { accountState ->
                val accountId = accountState.account?.accountId
                val accountChanged = accountId != activeTmdbAccountId
                activeTmdbAccountId = accountId
                _state.update { currentState ->
                    currentState.copy(
                        isTmdbAccountConnected = accountState.isConnected,
                        isWatchlisted = if (accountState.isConnected && !accountChanged) {
                            currentState.isWatchlisted
                        } else {
                            false
                        },
                        tmdbRating = if (accountState.isConnected && !accountChanged) {
                            currentState.tmdbRating
                        } else {
                            null
                        },
                        selectedTmdbRating = if (accountState.isConnected && !accountChanged) {
                            currentState.selectedTmdbRating
                        } else {
                            null
                        },
                        isRatingSheetVisible = currentState.isRatingSheetVisible &&
                            accountState.isConnected &&
                            !accountChanged,
                        isAccountActionLoading = false
                    )
                }

                if (accountState.isConnected) {
                    refreshMovieAccountState()
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
                    loadMovie(language = language)
                }
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.NavigateBack)
        }
    }

    private fun onFavoriteClick() {
        val currentState = state.value
        val movie = currentState.movie ?: return
        val wasFavorite = currentState.isFavorite

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isFavoriteLoading = true)
            }

            toggleFavoriteMovieUseCase(movie = movie).fold(
                onSuccess = {
                    _state.update { currentState ->
                        currentState.copy(isFavoriteLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = if (wasFavorite) {
                                "Removed from favorites"
                            } else {
                                "Added to favorites"
                            }
                        )
                    )
                },
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(isFavoriteLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = "Unable to update saved movie."
                        )
                    )
                }
            )
        }
    }

    private suspend fun refreshMovieAccountState() {
        getTmdbMovieAccountStateUseCase(movieId = movieId)
            .onSuccess { accountState ->
                _state.update { currentState ->
                    currentState.withTmdbAccountState(accountState = accountState)
                }
            }
            .onFailure {
                _effect.emit(
                    MovieDetailsEffect.ShowMessage(
                        title = "Sync failed",
                        message = "Couldn't connect to TMDB",
                        retryEvent = MovieDetailsEvent.AccountStateRetryClicked
                    )
                )
            }
    }

    private fun onWatchlistClicked() {
        val shouldWatchlist = !state.value.isWatchlisted
        viewModelScope.launch {
            if (state.value.isAccountActionLoading) return@launch
            if (!state.value.isTmdbAccountConnected) {
                _effect.emit(
                    MovieDetailsEffect.ShowMessage(
                        message = "Connect your TMDB account in Settings first."
                    )
                )
                return@launch
            }

            _state.update { currentState ->
                currentState.copy(isAccountActionLoading = true)
            }

            updateTmdbWatchlistUseCase(
                movieId = movieId,
                watchlist = shouldWatchlist
            ).fold(
                onSuccess = { accountState ->
                    _state.update { currentState ->
                        currentState.withTmdbAccountState(accountState = accountState).copy(
                            isAccountActionLoading = false,
                            isRatingSheetVisible = false
                        )
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = if (shouldWatchlist) {
                                "Added to TMDB watchlist"
                            } else {
                                "Removed from TMDB watchlist"
                            }
                        )
                    )
                },
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            title = "Sync failed",
                            message = "Couldn't update your TMDB watchlist",
                            retryEvent = MovieDetailsEvent.WatchlistClicked
                        )
                    )
                }
            )
        }
    }

    private fun onRatingClicked() {
        viewModelScope.launch {
            if (state.value.isAccountActionLoading) return@launch
            if (!state.value.isTmdbAccountConnected) {
                _effect.emit(
                    MovieDetailsEffect.ShowMessage(
                        message = "Connect your TMDB account in Settings first."
                    )
                )
                return@launch
            }

            _state.update { currentState ->
                currentState.copy(
                    selectedTmdbRating = currentState.tmdbRating,
                    isRatingSheetVisible = true
                )
            }
        }
    }

    private fun onRatingSelected(rating: Double) {
        _state.update { currentState ->
            currentState.copy(selectedTmdbRating = rating)
        }
    }

    private fun onRatingSheetDismissed() {
        _state.update { currentState ->
            currentState.copy(isRatingSheetVisible = false)
        }
    }

    private fun onRatingSubmitted() {
        viewModelScope.launch {
            if (state.value.isAccountActionLoading) return@launch
            if (!state.value.isTmdbAccountConnected) {
                _state.update { currentState ->
                    currentState.copy(isRatingSheetVisible = false)
                }
                _effect.emit(
                    MovieDetailsEffect.ShowMessage(
                        message = "Connect your TMDB account in Settings first."
                    )
                )
                return@launch
            }

            val rating = state.value.selectedTmdbRating ?: return@launch
            _state.update { currentState ->
                currentState.copy(isAccountActionLoading = true)
            }

            rateTmdbMovieUseCase(
                movieId = movieId,
                rating = rating
            ).fold(
                onSuccess = { accountState ->
                    _state.update { currentState ->
                        currentState.withTmdbAccountState(accountState = accountState).copy(
                            isAccountActionLoading = false,
                            isRatingSheetVisible = false
                        )
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = "Rated ${rating.toRatingText()} on TMDB"
                        )
                    )
                },
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            title = "Sync failed",
                            message = "Couldn't save your TMDB rating",
                            retryEvent = MovieDetailsEvent.RatingSubmitted
                        )
                    )
                }
            )
        }
    }

    private fun onRatingCleared() {
        if (state.value.tmdbRating == null) {
            _state.update { currentState ->
                currentState.copy(selectedTmdbRating = null)
            }
            return
        }

        viewModelScope.launch {
            if (state.value.isAccountActionLoading) return@launch
            if (!state.value.isTmdbAccountConnected) {
                _effect.emit(
                    MovieDetailsEffect.ShowMessage(
                        message = "Connect your TMDB account in Settings first."
                    )
                )
                return@launch
            }

            _state.update { currentState ->
                currentState.copy(isAccountActionLoading = true)
            }

            deleteTmdbMovieRatingUseCase(movieId = movieId).fold(
                onSuccess = { accountState ->
                    _state.update { currentState ->
                        currentState.withTmdbAccountState(accountState = accountState).copy(
                            selectedTmdbRating = null,
                            isAccountActionLoading = false
                        )
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = "TMDB rating removed"
                        )
                    )
                },
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            title = "Sync failed",
                            message = "Couldn't remove your TMDB rating",
                            retryEvent = MovieDetailsEvent.RatingCleared
                        )
                    )
                }
            )
        }
    }

    private fun onCastClicked(personId: Int) {
        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.NavigateToCastDetails(personId = personId))
        }
    }

    private fun onCastSaveClicked(personId: Int) {
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
                            MovieDetailsEffect.ShowMessage(
                                message = if (saved) {
                                    "Saved to library."
                                } else {
                                    "Removed from library."
                                }
                            )
                        )
                    },
                    onFailure = { error ->
                        _effect.emit(
                            MovieDetailsEffect.ShowMessage(
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

    private fun onCastSeeAllClicked() {
        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.NavigateToMovieCast(movieId = movieId))
        }
    }

    private fun onTrailerClicked(trailerId: String) {
        val trailerUrl = state.value.trailers
            .firstOrNull { trailer -> trailer.id == trailerId }
            ?.youtubeUrl
            ?: return

        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.OpenTrailer(url = trailerUrl))
        }
    }

    private fun onSimilarMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }

}
