package com.norbertotaveras.reels.features.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.navigation.ReelsDestinations
import com.norbertotaveras.reels.usecases.GetMovieCreditsUseCase
import com.norbertotaveras.reels.usecases.GetMovieDetailsUseCase
import com.norbertotaveras.reels.usecases.GetMovieVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
) : ViewModel() {
    val state = MovieDetailsUiState()
    private val movieId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.MOVIE_ID_ARG)
    )

    init {
        loadMovie()
    }

    fun loadMovie() {
        viewModelScope.launch {
            state.isLoading = true
            state.errorMessage = null

            val movieResult = getMovieDetailsUseCase(movieId = movieId)

            movieResult.fold(
                onSuccess = { movie ->
                    state.movie = movie

                    val creditsDeferred = async { getMovieCreditsUseCase(movieId = movieId) }
                    val videosDeferred = async { getMovieVideosUseCase(movieId = movieId) }

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
                                .filter { video ->
                                    video.site.equals("YouTube", ignoreCase = true) &&
                                            video.type.equals("Trailer", ignoreCase = true)
                                }
                                .take(10)
                        },
                        onFailure = {
                            state.trailers = emptyList()
                        }
                    )

                    state.isLoading = false
                },
                onFailure = { error ->
                    state.errorMessage = error.localizedMessage ?: "Unable to load movie details."
                    state.isLoading = false
                }
            )
        }
    }
}
