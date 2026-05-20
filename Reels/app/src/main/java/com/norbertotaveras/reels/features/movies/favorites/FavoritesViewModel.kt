package com.norbertotaveras.reels.features.movies.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.usecases.favorites.ObserveFavoriteMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase
) : ViewModel() {
    val state = FavoritesUiState()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteMoviesUseCase().collectLatest { movies ->
                state.movies = movies
            }
        }
    }
}
