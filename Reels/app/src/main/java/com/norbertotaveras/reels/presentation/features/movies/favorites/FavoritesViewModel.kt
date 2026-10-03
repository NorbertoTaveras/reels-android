/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.norbertotaveras.reels.domain.model.account.TmdbLibraryList
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.usecase.account.DeleteTmdbMovieRatingUseCase
import com.norbertotaveras.reels.domain.usecase.account.GetTmdbLibraryMoviesUseCase
import com.norbertotaveras.reels.domain.usecase.account.ObserveTmdbAccountUseCase
import com.norbertotaveras.reels.domain.usecase.account.ObserveTmdbMovieActivitiesUseCase
import com.norbertotaveras.reels.domain.usecase.account.UpdateTmdbWatchlistUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveLibraryContentUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.RemoveFavoriteMovieUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.RemoveSavedPersonUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModel @Inject constructor(
    private val observeLibraryContentUseCase: ObserveLibraryContentUseCase,
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase,
    private val removeSavedPersonUseCase: RemoveSavedPersonUseCase,
    private val observeTmdbAccountUseCase: ObserveTmdbAccountUseCase,
    private val observeTmdbMovieActivitiesUseCase: ObserveTmdbMovieActivitiesUseCase,
    private val getTmdbLibraryMoviesUseCase: GetTmdbLibraryMoviesUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateTmdbWatchlistUseCase: UpdateTmdbWatchlistUseCase,
    private val deleteTmdbMovieRatingUseCase: DeleteTmdbMovieRatingUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FavoritesEffect>()
    val effect: SharedFlow<FavoritesEffect> = _effect.asSharedFlow()

    private val tmdbRefreshRequests = MutableStateFlow(0)
    private val language = observeSettingsUseCase()
        .map { settings -> settings.languageRegion.languageCode }
        .distinctUntilChanged()

    val tmdbWatchlistMovies: Flow<PagingData<MovieDataModel>> = combine(
        language,
        tmdbRefreshRequests
    ) { language, _ -> language }
        .flatMapLatest { language ->
            getTmdbLibraryMoviesUseCase(
                list = TmdbLibraryList.Watchlist,
                language = language
            )
        }
        .cachedIn(viewModelScope)

    val tmdbRatedMovies: Flow<PagingData<MovieDataModel>> = combine(
        language,
        tmdbRefreshRequests
    ) { language, _ -> language }
        .flatMapLatest { language ->
            getTmdbLibraryMoviesUseCase(
                list = TmdbLibraryList.Rated,
                language = language
            )
        }
        .cachedIn(viewModelScope)

    init {
        observeLibraryContent()
        observeTmdbAccount()
        observeTmdbMovieActivities()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.TabSelected -> onTabSelected(tab = event.tab)
            is FavoritesEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
            is FavoritesEvent.PersonClicked -> onPersonClicked(personId = event.personId)
            is FavoritesEvent.FavoriteClicked -> onFavoriteClicked(movieId = event.movieId)
            is FavoritesEvent.SavedPersonClicked -> onSavedPersonClicked(personId = event.personId)
            is FavoritesEvent.WatchlistMovieRemoved -> removeFromTmdbWatchlist(
                movieId = event.movieId
            )
            is FavoritesEvent.RatedMovieRemoved -> removeTmdbRating(movieId = event.movieId)
            FavoritesEvent.ConnectSettingsClicked -> onConnectSettingsClicked()
        }
    }

    private fun observeLibraryContent() {
        viewModelScope.launch {
            observeLibraryContentUseCase().collectLatest { content ->
                _state.update { currentState ->
                    currentState.copy(
                        movies = content.movies,
                        people = content.people
                    )
                }
            }
        }
    }

    private fun observeTmdbAccount() {
        viewModelScope.launch {
            observeTmdbAccountUseCase().collectLatest { accountState ->
                _state.update { currentState ->
                    currentState.copy(
                        isTmdbAccountConnected = accountState.isConnected
                    )
                }
            }
        }
    }

    private fun observeTmdbMovieActivities() {
        viewModelScope.launch {
            observeTmdbMovieActivitiesUseCase().collectLatest { activities ->
                _state.update { currentState ->
                    currentState.copy(
                        tmdbActivityTimes = activities
                            .groupBy { activity -> activity.listType }
                            .mapValues { (_, listActivities) ->
                                listActivities.associate { activity ->
                                    activity.movieId to activity.occurredAtMillis
                                }
                            }
                    )
                }
            }
        }
    }

    private fun onTabSelected(tab: LibraryTab) {
        _state.update { currentState ->
            currentState.copy(selectedTab = tab)
        }
        if (tab == LibraryTab.Tmdb || tab == LibraryTab.Rated) {
            refreshTmdbLists()
        }
    }

    private fun refreshTmdbLists() {
        if (!_state.value.isTmdbAccountConnected) return
        tmdbRefreshRequests.update { refreshCount -> refreshCount + 1 }
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

    private fun onConnectSettingsClicked() {
        viewModelScope.launch {
            _effect.emit(FavoritesEffect.NavigateToSettings)
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

    private fun removeFromTmdbWatchlist(movieId: Int) {
        updateTmdbMovie(
            movieId = movieId,
            successMessage = "Removed from TMDB watchlist.",
            failureMessage = "Unable to update TMDB watchlist."
        ) {
            updateTmdbWatchlistUseCase(movieId = movieId, watchlist = false)
        }
    }

    private fun removeTmdbRating(movieId: Int) {
        updateTmdbMovie(
            movieId = movieId,
            successMessage = "Removed TMDB rating.",
            failureMessage = "Unable to remove TMDB rating."
        ) {
            deleteTmdbMovieRatingUseCase(movieId = movieId)
        }
    }

    private fun updateTmdbMovie(
        movieId: Int,
        successMessage: String,
        failureMessage: String,
        update: suspend () -> Result<*>,
    ) {
        viewModelScope.launch {
            if (_state.value.updatingTmdbMovieIds.contains(movieId)) return@launch

            _state.update { currentState ->
                currentState.copy(
                    updatingTmdbMovieIds = currentState.updatingTmdbMovieIds + movieId
                )
            }

            try {
                update().onSuccess {
                    refreshTmdbLists()
                    _effect.emit(FavoritesEffect.ShowMessage(message = successMessage))
                }.onFailure { error ->
                    _effect.emit(
                        FavoritesEffect.ShowMessage(
                            message = error.toUserMessage(fallback = failureMessage)
                        )
                    )
                }
            } finally {
                _state.update { currentState ->
                    currentState.copy(
                        updatingTmdbMovieIds = currentState.updatingTmdbMovieIds - movieId
                    )
                }
            }
        }
    }
}
