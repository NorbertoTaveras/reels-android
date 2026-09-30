/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.cast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme
import com.norbertotaveras.reels.presentation.ui.theme.reelsPlaceholderColor
import com.norbertotaveras.reels.presentation.ui.theme.reelsMutedContainerColor

@Composable
internal fun MovieCastStateContent(
    state: MovieCastUiState,
    onEvent: (MovieCastEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            MovieCastLoadingState(
                modifier = modifier,
                onBackClick = {
                    onEvent(MovieCastEvent.BackClicked)
                }
            )
        }

        state.errorMessage != null -> {
            MovieCastErrorState(
                modifier = modifier,
                message = state.errorMessage ?: "Unable to load full cast.",
                retryLabel = "Retry",
                onBackClick = {
                    onEvent(MovieCastEvent.BackClicked)
                },
                onRetry = {
                    onEvent(MovieCastEvent.RetryClicked)
                }
            )
        }

        else -> {
            MovieCastContent(
                modifier = modifier,
                cast = state.cast,
                savedPersonIds = state.savedPersonIds,
                savingPersonIds = state.savingPersonIds,
                onBackClick = {
                    onEvent(MovieCastEvent.BackClicked)
                },
                onCastClick = { personId ->
                    onEvent(MovieCastEvent.CastClicked(personId = personId))
                },
                onSaveClick = { personId ->
                    onEvent(MovieCastEvent.SaveClicked(personId = personId))
                }
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun MovieCastScreenPreview() {
    ReelsTheme {
        MovieCastScreen(
            state = MovieCastUiState(
                isLoading = false,
                cast = listOf(
                    CastMemberDataModel(
                        id = 1,
                        name = "Timothée Chalamet",
                        characterName = "Paul Atreides",
                        profilePath = null
                    ),
                    CastMemberDataModel(
                        id = 2,
                        name = "Zendaya",
                        characterName = "Chani",
                        profilePath = null
                    ),
                    CastMemberDataModel(
                        id = 3,
                        name = "Rebecca Ferguson",
                        characterName = "Lady Jessica",
                        profilePath = null
                    )
                )
            ),
            onEvent = {}
        )
    }
}
