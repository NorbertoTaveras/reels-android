/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.filmography

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import com.norbertotaveras.reels.domain.usecase.person.GetPersonDetailsUseCase
import com.norbertotaveras.reels.domain.usecase.person.GetPersonMovieCreditsUseCase
import com.norbertotaveras.reels.domain.usecase.settings.ObserveSettingsUseCase
import com.norbertotaveras.reels.presentation.navigation.ReelsDestinations
import com.norbertotaveras.reels.presentation.ui.extensions.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FilmographyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPersonDetailsUseCase: GetPersonDetailsUseCase,
    private val getPersonMovieCreditsUseCase: GetPersonMovieCreditsUseCase,
    private val observeSettingsUseCase: ObserveSettingsUseCase,
) : ViewModel() {
    private val personId: Int = checkNotNull(
        savedStateHandle.get<Int>(ReelsDestinations.PERSON_ID_ARG)
    )

    private val _state = MutableStateFlow(FilmographyUiState())
    val state: StateFlow<FilmographyUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FilmographyEffect>()
    val effect: SharedFlow<FilmographyEffect> = _effect.asSharedFlow()

    private var currentLanguage: String = DEFAULT_LANGUAGE
    private var loadFilmographyJob: Job? = null

    init {
        observeSettings()
    }

    fun onEvent(event: FilmographyEvent) {
        when (event) {
            FilmographyEvent.BackClicked -> onBackClicked()
            FilmographyEvent.RetryClicked -> loadFilmography(language = currentLanguage)
            is FilmographyEvent.SortSelected -> onSortSelected(sort = event.sort)
            is FilmographyEvent.MovieClicked -> onMovieClicked(movieId = event.movieId)
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeSettingsUseCase()
                .map { settings -> settings.languageRegion.languageCode }
                .distinctUntilChanged()
                .collect { language ->
                    currentLanguage = language
                    loadFilmography(language = language)
                }
        }
    }

    private fun loadFilmography(language: String) {
        loadFilmographyJob?.cancel()
        loadFilmographyJob = viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            val personDeferred = async {
                getPersonDetailsUseCase(
                    personId = personId,
                    language = language
                )
            }

            val creditsDeferred = async {
                getPersonMovieCreditsUseCase(
                    personId = personId,
                    language = language
                )
            }

            val personResult = personDeferred.await()
            val creditsResult = creditsDeferred.await()

            _state.update { currentState ->
                creditsResult.fold(
                    onSuccess = { credits ->
                        currentState.copy(
                            personName = personResult.getOrNull()?.name ?: currentState.personName,
                            movies = credits.cast.sortedBy(sort = currentState.sort),
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.toUserMessage(
                                fallback = "Unable to load filmography."
                            )
                        )
                    }
                )
            }
        }
    }

    private fun onSortSelected(sort: FilmographySort) {
        _state.update { currentState ->
            currentState.copy(
                sort = sort,
                movies = currentState.movies.sortedBy(sort = sort)
            )
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch {
            _effect.emit(FilmographyEffect.NavigateBack)
        }
    }

    private fun onMovieClicked(movieId: Int) {
        viewModelScope.launch {
            _effect.emit(FilmographyEffect.NavigateToMovieDetails(movieId = movieId))
        }
    }

    private companion object {
        const val DEFAULT_LANGUAGE = "en-US"
    }
}

private fun List<PersonMovieCreditDataModel>.sortedBy(
    sort: FilmographySort,
): List<PersonMovieCreditDataModel> {
    return when (sort) {
        FilmographySort.Popular -> sortedByDescending { movie -> movie.popularity }
        FilmographySort.Newest -> sortedWith(
            compareByDescending<PersonMovieCreditDataModel> { movie ->
                movie.releaseDate?.take(4)?.toIntOrNull() ?: 0
            }.thenByDescending { movie -> movie.popularity }
        )
        FilmographySort.HighestRated -> sortedWith(
            compareByDescending<PersonMovieCreditDataModel> { movie -> movie.voteAverage }
                .thenByDescending { movie -> movie.popularity }
        )
    }
}
