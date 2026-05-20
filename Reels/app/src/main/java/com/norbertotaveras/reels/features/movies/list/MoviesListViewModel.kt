package com.norbertotaveras.reels.features.movies.list

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.norbertotaveras.reels.model.constant.MovieCategory
import com.norbertotaveras.reels.usecases.movie.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MoviesListViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {
    val state = MoviesListState()

    private val _effect = MutableSharedFlow<MoviesListEffect>()
    val effect: SharedFlow<MoviesListEffect> = _effect.asSharedFlow()

    val movies = snapshotFlow { state.category to state.language }
        .distinctUntilChanged()
        .flatMapLatest { (category, language) ->
            getMoviesUseCase(
                category = category,
                language = language
            )
        }
        .cachedIn(viewModelScope)

    fun onEvent(event: MoviesListEvent) {
        when (event) {
            is MoviesListEvent.CategorySelected -> onCategorySelected(category = event.category)
            is MoviesListEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    private fun onCategorySelected(category: MovieCategory) {
        state.category = category
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(MoviesListEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }
}
