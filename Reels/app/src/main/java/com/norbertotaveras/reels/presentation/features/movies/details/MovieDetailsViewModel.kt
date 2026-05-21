package com.norbertotaveras.reels.presentation.features.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import com.norbertotaveras.reels.domain.usecase.favorites.GetFavoriteMovieDetailsUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ObserveIsFavoriteUseCase
import com.norbertotaveras.reels.domain.usecase.favorites.ToggleFavoriteMovieUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieCreditsUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieDetailsUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetMovieVideosUseCase
import com.norbertotaveras.reels.domain.usecase.movie.GetSimilarMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailsUiState())
    val state: StateFlow<MovieDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MovieDetailsEffect>()
    val effect: SharedFlow<MovieDetailsEffect> = _effect.asSharedFlow()

    private val movieId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.MOVIE_ID_ARG)
    )

    init {
        observeFavoriteState()
        loadMovie()
    }

    fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            MovieDetailsEvent.BackClicked -> onBackClicked()
            MovieDetailsEvent.RetryClicked -> loadMovie()
            MovieDetailsEvent.FavoriteClicked -> onFavoriteClick()
            is MovieDetailsEvent.CastClicked -> onCastClicked(personId = event.personId)
            MovieDetailsEvent.CastSeeAllClicked -> Unit
            is MovieDetailsEvent.TrailerClicked -> onTrailerClicked(trailerId = event.trailerId)
            is MovieDetailsEvent.SimilarMovieClicked -> onSimilarMovieClicked(movieId = event.movieId)
        }
    }

    fun loadMovie() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val movieResult = getMovieDetailsUseCase(movieId = movieId)

            movieResult.fold(
                onSuccess = { movie ->
                    _state.update { currentState ->
                        currentState.copy(
                            movie = movie,
                            isOfflineFavorite = false
                        )
                    }

                    val creditsDeferred = async { getMovieCreditsUseCase(movieId = movieId) }
                    val videosDeferred = async { getMovieVideosUseCase(movieId = movieId) }
                    val similarDeferred = async { getSimilarMoviesUseCase(movieId = movieId) }

                    creditsDeferred.await().fold(
                        onSuccess = { credits ->
                            _state.update { currentState ->
                                currentState.copy(cast = credits.cast.take(10))
                            }
                        },
                        onFailure = {
                            _state.update { currentState ->
                                currentState.copy(cast = emptyList())
                            }
                        }
                    )

                    videosDeferred.await().fold(
                        onSuccess = { videos ->
                            _state.update { currentState ->
                                currentState.copy(
                                    trailers = videos.videos
                                        .filter { video -> video.isPlayableTrailer }
                                        .take(10)
                                )
                            }
                        },
                        onFailure = {
                            _state.update { currentState ->
                                currentState.copy(trailers = emptyList())
                            }
                        }
                    )

                    similarDeferred.await().fold(
                        onSuccess = { movies ->
                            _state.update { currentState ->
                                currentState.copy(similarMovies = movies.take(10))
                            }
                        },
                        onFailure = {
                            _state.update { currentState ->
                                currentState.copy(similarMovies = emptyList())
                            }
                        }
                    )

                    _state.update { currentState ->
                        currentState.copy(isLoading = false)
                    }
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
                                errorMessage = error.localizedMessage ?: "Unable to load movie details.",
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

    private fun onBackClicked() {
        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.NavigateBack)
        }
    }

    private fun onFavoriteClick() {
        val movie = state.value.movie ?: return

        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isFavoriteLoading = true)
            }

            toggleFavoriteMovieUseCase(movie = movie).fold(
                onSuccess = {
                    _state.update { currentState ->
                        currentState.copy(isFavoriteLoading = false)
                    }
                },
                onFailure = {
                    _state.update { currentState ->
                        currentState.copy(isFavoriteLoading = false)
                    }
                }
            )
        }
    }

    private fun onCastClicked(personId: Int) {
        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.NavigateToCastDetails(personId = personId))
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
