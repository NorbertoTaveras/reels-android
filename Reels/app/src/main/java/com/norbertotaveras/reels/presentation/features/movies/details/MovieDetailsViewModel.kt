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
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import com.norbertotaveras.reels.domain.usecase.account.DeleteTmdbMovieRatingUseCase
import com.norbertotaveras.reels.domain.usecase.account.GetTmdbMovieAccountStateUseCase
import com.norbertotaveras.reels.domain.usecase.account.ObserveTmdbAccountUseCase
import com.norbertotaveras.reels.domain.usecase.account.RateTmdbMovieUseCase
import com.norbertotaveras.reels.domain.usecase.account.UpdateTmdbWatchlistUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.GetFavoriteMovieDetailsUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveIsFavoriteUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ToggleFavoriteMovieUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieCreditsUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieDetailsUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieVideosUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetSimilarMoviesUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    private val getSimilarMoviesUseCase: GetSimilarMoviesUseCase,
    private val observeIsFavoriteUseCase: ObserveIsFavoriteUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
    private val getFavoriteMovieDetailsUseCase: GetFavoriteMovieDetailsUseCase,
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
    private var currentLanguage: String = DEFAULT_LANGUAGE
    private var loadMovieJob: Job? = null

    init {
        observeFavoriteState()
        observeTmdbAccountState()
        observeSettings()
    }

    fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            MovieDetailsEvent.BackClicked -> onBackClicked()
            MovieDetailsEvent.RetryClicked -> loadMovie(language = currentLanguage)
            MovieDetailsEvent.FavoriteClicked -> onFavoriteClick()
            MovieDetailsEvent.WatchlistClicked -> onWatchlistClicked()
            MovieDetailsEvent.RatingClicked -> onRatingClicked()
            MovieDetailsEvent.DeleteRatingClicked -> onDeleteRatingClicked()
            is MovieDetailsEvent.CastClicked -> onCastClicked(personId = event.personId)
            MovieDetailsEvent.CastSeeAllClicked -> onCastSeeAllClicked()
            is MovieDetailsEvent.TrailerClicked -> onTrailerClicked(trailerId = event.trailerId)
            is MovieDetailsEvent.SimilarMovieClicked -> onSimilarMovieClicked(movieId = event.movieId)
        }
    }

    fun loadMovie(language: String = currentLanguage) {
        loadMovieJob?.cancel()
        loadMovieJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val movieResult = getMovieDetailsUseCase(
                movieId = movieId,
                language = language
            )

            movieResult.fold(
                onSuccess = { movie ->
                    val creditsDeferred = async {
                        getMovieCreditsUseCase(
                            movieId = movieId,
                            language = language
                        )
                    }
                    val videosDeferred = async {
                        getMovieVideosUseCase(
                            movieId = movieId,
                            language = language
                        )
                    }
                    val similarDeferred = async {
                        getSimilarMoviesUseCase(
                            movieId = movieId,
                            language = language
                        )
                    }

                    val cast = creditsDeferred.await()
                        .getOrNull()
                        ?.cast
                        ?.take(10)
                        .orEmpty()
                    val trailers = videosDeferred.await()
                        .getOrNull()
                        ?.videos
                        ?.filter { video -> video.isPlayableTrailer }
                        ?.take(10)
                        .orEmpty()
                    val similarMovies = similarDeferred.await()
                        .getOrNull()
                        ?.take(10)
                        .orEmpty()

                    _state.update { currentState ->
                        currentState.copy(
                            movie = movie,
                            cast = cast,
                            trailers = trailers,
                            similarMovies = similarMovies,
                            isOfflineFavorite = false,
                            isLoading = false
                        )
                    }
                    refreshMovieAccountState()
                },
                onFailure = { error ->
                    val offlineFavorite = getFavoriteMovieDetailsUseCase(movieId = movieId)
                    _state.update { currentState ->
                        if (offlineFavorite != null) {
                            currentState.copy(
                                movie = offlineFavorite,
                                cast = emptyList(),
                                trailers = emptyList(),
                                similarMovies = emptyList(),
                                isOfflineFavorite = true,
                                errorMessage = null,
                                isLoading = false
                            )
                        } else {
                            currentState.copy(
                                errorMessage = error.toUserMessage(
                                    fallback = "Unable to load movie details."
                                ),
                                isLoading = false
                            )
                        }
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

    private fun observeTmdbAccountState() {
        viewModelScope.launch {
            observeTmdbAccountUseCase().collectLatest { accountState ->
                _state.update { currentState ->
                    currentState.copy(
                        isTmdbAccountConnected = accountState.isConnected,
                        isWatchlisted = if (accountState.isConnected) {
                            currentState.isWatchlisted
                        } else {
                            false
                        },
                        tmdbRating = if (accountState.isConnected) {
                            currentState.tmdbRating
                        } else {
                            null
                        }
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
                    currentState.copy(
                        isWatchlisted = accountState?.isWatchlisted ?: false,
                        tmdbRating = accountState?.rating
                    )
                }
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
                onSuccess = {
                    refreshMovieAccountState()
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
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
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Unable to update TMDB watchlist."
                            )
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
                currentState.copy(isAccountActionLoading = true)
            }

            rateTmdbMovieUseCase(
                movieId = movieId,
                rating = DEFAULT_TMDB_RATING
            ).fold(
                onSuccess = {
                    refreshMovieAccountState()
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = "Rated on TMDB"
                        )
                    )
                },
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Unable to rate this movie."
                            )
                        )
                    )
                }
            )
        }
    }

    private fun onDeleteRatingClicked() {
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
                onSuccess = {
                    refreshMovieAccountState()
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = "TMDB rating removed"
                        )
                    )
                },
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(isAccountActionLoading = false)
                    }
                    _effect.emit(
                        MovieDetailsEffect.ShowMessage(
                            message = error.toUserMessage(
                                fallback = "Unable to remove this rating."
                            )
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

    private companion object {
        const val DEFAULT_LANGUAGE = "en-US"
        const val DEFAULT_TMDB_RATING = 8.0
    }
}
