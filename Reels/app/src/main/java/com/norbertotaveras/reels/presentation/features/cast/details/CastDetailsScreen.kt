package com.norbertotaveras.reels.presentation.features.cast.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredErrorState
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredLoadingState
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl

@Composable
fun CastDetailsScreen(
    state: CastDetailsUiState,
    onEvent: (CastDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            CenteredLoadingState(modifier = modifier)
        }

        state.errorMessage != null -> {
            CenteredErrorState(
                modifier = modifier,
                message = state.errorMessage ?: "Unable to load cast details.",
                retryLabel = "Retry",
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
                profileUrl = person.profilePath.toTmdbImageUrl(),
                biography = person.biography,
                knownForDepartment = person.knownForDepartment,
                knownFor = state.knownFor,
                birthday = person.birthday,
                placeOfBirth = person.placeOfBirth,
                onBackClick = {
                    onEvent(CastDetailsEvent.BackClicked)
                },
                onMovieClick = { movieId ->
                    onEvent(CastDetailsEvent.MovieClicked(movieId = movieId))
                },
                onFavoriteClick = {
                    onEvent(CastDetailsEvent.FavoriteClicked)
                }
            )
        }
    }
}
