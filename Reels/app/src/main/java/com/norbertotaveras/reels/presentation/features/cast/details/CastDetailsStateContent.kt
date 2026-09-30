/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor

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
