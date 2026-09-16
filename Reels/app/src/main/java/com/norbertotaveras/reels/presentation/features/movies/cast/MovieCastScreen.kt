package com.norbertotaveras.reels.presentation.features.movies.cast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredErrorState
import com.norbertotaveras.reels.presentation.ui.components.common.CenteredLoadingState
import com.norbertotaveras.reels.presentation.ui.components.common.EmptyState
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsBorder
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsRatios
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

@Composable
fun MovieCastScreen(
    state: MovieCastUiState,
    onEvent: (MovieCastEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            CenteredLoadingState(modifier = modifier)
        }

        state.errorMessage != null -> {
            CenteredErrorState(
                modifier = modifier,
                message = state.errorMessage ?: "Unable to load full cast.",
                retryLabel = "Retry",
                onRetry = {
                    onEvent(MovieCastEvent.RetryClicked)
                }
            )
        }

        else -> {
            MovieCastContent(
                modifier = modifier,
                cast = state.cast,
                onBackClick = {
                    onEvent(MovieCastEvent.BackClicked)
                },
                onCastClick = { personId ->
                    onEvent(MovieCastEvent.CastClicked(personId = personId))
                }
            )
        }
    }
}

@Composable
private fun MovieCastContent(
    cast: List<CastMemberDataModel>,
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = ReelsSpacing.screenHorizontal,
            top = ReelsDimensions.fullCastScreenTopPadding,
            end = ReelsSpacing.screenHorizontal,
            bottom = ReelsDimensions.fullCastScreenBottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.fullCastRowSpacing)
    ) {
        item {
            MovieCastHeader(
                modifier = Modifier.padding(bottom = ReelsDimensions.fullCastHeaderBottomPadding),
                onBackClick = onBackClick
            )
        }

        if (cast.isEmpty()) {
            item {
                EmptyState(
                    title = "No cast found",
                    message = "This movie does not have cast information available yet."
                )
            }
        } else {
            items(
                items = cast,
                key = { castMember -> castMember.id }
            ) { castMember ->
                MovieCastRow(
                    castMember = castMember,
                    onClick = {
                        onCastClick(castMember.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun MovieCastHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.fullCastHeaderSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.fullCastBackButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = ReelsDimensions.fullCastBorderWidth,
                color = ReelsBorder
            )
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(ReelsDimensions.fullCastBackIconSize)
                )
            }
        }

        Text(
            text = "Full Cast",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MovieCastRow(
    castMember: CastMemberDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = ReelsDimensions.fullCastBorderWidth,
            color = ReelsBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(ReelsDimensions.fullCastRowPadding),
            horizontalArrangement = Arrangement.spacedBy(ReelsDimensions.fullCastRowContentSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = castMember.profilePath.toTmdbImageUrl(),
                contentDescription = castMember.name,
                modifier = Modifier
                    .width(ReelsDimensions.fullCastProfileWidth)
                    .aspectRatio(ReelsRatios.poster)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.fullCastRowTextSpacing)
            ) {
                Text(
                    text = castMember.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = castMember.characterName.ifBlank { "Character unavailable" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
