package com.norbertotaveras.reels.features.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.navigation.ReelsDestinations
import com.norbertotaveras.reels.usecases.GetMovieCreditsUseCase
import com.norbertotaveras.reels.usecases.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieCreditsUseCase: GetMovieCreditsUseCase,
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
                    state.isLoading = false
                    loadCredits()
                },
                onFailure = { error ->
                    state.errorMessage = error.localizedMessage ?: "Unable to load movie details."
                    state.isLoading = false
                }
            )
        }
    }

    private fun loadCredits() {
        viewModelScope.launch {
            getMovieCreditsUseCase(movieId = movieId).fold(
                onSuccess = { credits ->
                    state.cast = credits.cast.take(10)
                },
                onFailure = {
                    state.cast = emptyList()
                }
            )
        }
    }

}
