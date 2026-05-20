package com.norbertotaveras.reels.features.movies.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.usecases.favorites.ObserveFavoriteMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase
) : ViewModel() {
    val state = FavoritesUiState()

    private val _effect = MutableSharedFlow<FavoritesEffect>()
    val effect: SharedFlow<FavoritesEffect> = _effect.asSharedFlow()

    init {
        observeFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteMoviesUseCase().collectLatest { movies ->
                state.movies = movies
            }
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(FavoritesEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }
}
