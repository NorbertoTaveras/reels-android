package com.norbertotaveras.reels.features.movies.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.navigation.CinematicsDestinations
import com.norbertotaveras.reels.usecases.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {
    val state = MovieDetailsUiState()
    private val movieId: Int = checkNotNull(
        savedStateHandle.get<Int>(CinematicsDestinations.MOVIE_ID_ARG)
    )

    init {
        loadMovie()
    }

    fun loadMovie() {
        viewModelScope.launch {
            state.isLoading = true
            state.errorMessage = null

            getMovieDetailsUseCase(movieId = movieId).fold(
                onSuccess = { movie ->
                    state.movie = movie
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
