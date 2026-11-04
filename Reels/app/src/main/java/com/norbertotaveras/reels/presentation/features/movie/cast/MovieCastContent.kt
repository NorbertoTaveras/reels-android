/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.cast

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.norbertotaveras.reels.domain.person.model.CastMemberDataModel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

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
