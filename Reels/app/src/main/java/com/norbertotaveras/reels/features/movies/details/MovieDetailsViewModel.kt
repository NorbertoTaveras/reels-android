package com.norbertotaveras.reels.features.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.navigation.ReelsDestinations
import com.norbertotaveras.reels.usecases.favorites.GetFavoriteMovieDetailsUseCase
import com.norbertotaveras.reels.usecases.favorites.ObserveIsFavoriteUseCase
import com.norbertotaveras.reels.usecases.favorites.ToggleFavoriteMovieUseCase
import com.norbertotaveras.reels.usecases.movie.GetMovieCreditsUseCase
import com.norbertotaveras.reels.usecases.movie.GetMovieDetailsUseCase
import com.norbertotaveras.reels.usecases.movie.GetMovieVideosUseCase
import com.norbertotaveras.reels.usecases.movie.GetSimilarMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
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
    val state = MovieDetailsUiState()

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
            state.isLoading = true
            state.errorMessage = null

            val movieResult = getMovieDetailsUseCase(movieId = movieId)

            movieResult.fold(
                onSuccess = { movie ->
                    state.movie = movie
                    state.isOfflineFavorite = false
                    val creditsDeferred = async { getMovieCreditsUseCase(movieId = movieId) }
                    val videosDeferred = async { getMovieVideosUseCase(movieId = movieId) }
                    val similarDeferred = async { getSimilarMoviesUseCase(movieId = movieId) }

                    creditsDeferred.await().fold(
                        onSuccess = { credits ->
                            state.cast = credits.cast.take(10)
                        },
                        onFailure = {
                            state.cast = emptyList()
                        }
                    )

                    videosDeferred.await().fold(
                        onSuccess = { videos ->
                            state.trailers = videos.videos
                                .filter { video -> video.isPlayableTrailer }
                                .take(10)
                        },
                        onFailure = {
                            state.trailers = emptyList()
                        }
                    )

                    similarDeferred.await().fold(
                        onSuccess = { movies ->
                            state.similarMovies = movies.take(10)
                        },
                        onFailure = {
                            state.similarMovies = emptyList()
                        }
                    )

                    state.isLoading = false
                },
                onFailure = { error ->
                    val offlineFavorite = getFavoriteMovieDetailsUseCase(movieId = movieId)
                    state.apply {
                        if (offlineFavorite != null) {
                            movie = offlineFavorite
                            cast = emptyList()
                            trailers = emptyList()
                            similarMovies = emptyList()
                            isOfflineFavorite = true
                            errorMessage = null
                        } else {
                            errorMessage = error.localizedMessage ?: "Unable to load movie details."
                        }
                        isLoading = false
                    }
                }
            )
        }
    }

    private fun observeFavoriteState() {
        viewModelScope.launch {
            observeIsFavoriteUseCase(movieId = movieId).collectLatest { isFavorite ->
                state.isFavorite = isFavorite
            }
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _effect.emit(MovieDetailsEffect.NavigateBack)
        }
    }

    private fun onFavoriteClick() {
        val movie = state.movie ?: return

        viewModelScope.launch {
            state.isFavoriteLoading = true

            toggleFavoriteMovieUseCase(movie = movie).fold(
                onSuccess = {
                    state.isFavoriteLoading = false
                },
                onFailure = {
                    state.isFavoriteLoading = false
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
        val trailerUrl = state.trailers
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
