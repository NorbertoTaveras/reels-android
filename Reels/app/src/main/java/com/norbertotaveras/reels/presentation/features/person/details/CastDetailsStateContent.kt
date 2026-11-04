/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.person.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl

@Composable
internal fun CastDetailsStateContent(
    state: CastDetailsUiState,
    onEvent: (CastDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            CastDetailsLoadingState(
                modifier = modifier,
                onBackClick = {
                    onEvent(CastDetailsEvent.BackClicked)
                }
            )
        }

        state.errorMessage != null -> {
            CastDetailsErrorState(
                modifier = modifier,
                message = state.errorMessage ?: "Unable to load cast details.",
                retryLabel = "Retry",
                onBackClick = {
                    onEvent(CastDetailsEvent.BackClicked)
                },
                onRetry = {
                    onEvent(CastDetailsEvent.RetryClicked)
                }
            )
        }

        state.person != null -> {
            val person = checkNotNull(state.person)

            CastDetailsContent(
                modifier = modifier,
                name = person.name,
                profileUrl = person.profilePath.toTmdbImageUrl(
                    imageType = TmdbImageType.Profile
                ),
                biography = person.biography,
                knownForDepartment = person.knownForDepartment,
                knownFor = state.knownFor,
                birthday = person.birthday,
                placeOfBirth = person.placeOfBirth,
                isFavorite = state.isSaved,
                isFavoriteLoading = state.isSaving,
                isOfflineSavedPerson = state.isOfflineSavedPerson,
                onBackClick = {
                    onEvent(CastDetailsEvent.BackClicked)
                },
                onMovieClick = { movieId ->
                    onEvent(CastDetailsEvent.MovieClicked(movieId = movieId))
                },
                onFilmographyClick = {
                    onEvent(CastDetailsEvent.FilmographyClicked)
                },
                onFavoriteClick = {
                    onEvent(CastDetailsEvent.FavoriteClicked)
                }
            )
        }
    }
}
