package com.norbertotaveras.reels.features.cast.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.norbertotaveras.reels.ui.components.CenteredErrorState
import com.norbertotaveras.reels.ui.components.CenteredLoadingState
import com.norbertotaveras.reels.ui.extensions.toTmdbImageUrl

@Composable
fun CastDetailsScreen(
    state: CastDetailsUiState,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit,
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
                onRetry = onRetry
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
                onBackClick = onBackClick,
                onMovieClick = onMovieClick
            )
        }
    }
}

