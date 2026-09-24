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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.norbertotaveras.reels.presentation.ui.extensions.toErrorTitle
import com.norbertotaveras.reels.presentation.ui.extensions.TmdbImageType
import com.norbertotaveras.reels.presentation.ui.extensions.toTmdbImageUrl
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing

@Composable
fun CastDetailsScreen(
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
                onFavoriteClick = {
                    onEvent(CastDetailsEvent.FavoriteClicked)
                }
            )
        }
    }
}

@Composable
private fun CastDetailsLoadingState(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = ReelsSpacing.xxLarge),
        verticalArrangement = Arrangement.spacedBy(ReelsSpacing.section)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ReelsDimensions.castDetailsHeroHeight)
                    .background(castDetailsPlaceholderColor())
            ) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = ReelsSpacing.screenHorizontal,
                            top = ReelsDimensions.castDetailsHeroActionTopPadding
                        )
                        .size(ReelsDimensions.castDetailsHeroActionButtonSize),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }

                CastDetailsPlaceholderBar(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = ReelsSpacing.screenHorizontal, bottom = 76.dp),
                    width = 220.dp,
                    height = 28.dp
                )

                CastDetailsPlaceholderBar(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = ReelsSpacing.screenHorizontal, bottom = 44.dp),
                    width = 92.dp,
                    height = 16.dp
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.castDetailsQuickInfoSpacing)
            ) {
                repeat(2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CastDetailsPlaceholderBox(
                            modifier = Modifier.size(ReelsDimensions.castDetailsInfoIconContainerSize)
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.xSmall)) {
                            CastDetailsPlaceholderBar(width = 82.dp, height = 14.dp)
                            CastDetailsPlaceholderBar(width = 190.dp, height = 18.dp)
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ReelsSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                CastDetailsPlaceholderBar(width = 116.dp, height = 24.dp)
                CastDetailsPlaceholderBar(width = 308.dp, height = 18.dp)
                CastDetailsPlaceholderBar(width = 294.dp, height = 18.dp)
                CastDetailsPlaceholderBar(width = 236.dp, height = 18.dp)
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = ReelsSpacing.screenHorizontal),
                horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium)
            ) {
                items(CAST_DETAILS_KNOWN_FOR_PLACEHOLDER_COUNT) {
                    Column(verticalArrangement = Arrangement.spacedBy(ReelsSpacing.small)) {
                        CastDetailsPlaceholderBox(
                            modifier = Modifier
                                .width(ReelsDimensions.posterCardWidth)
                                .height(178.dp)
                        )
                        CastDetailsPlaceholderBar(width = 106.dp, height = 16.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CastDetailsErrorState(
    message: String,
    retryLabel: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = ReelsSpacing.screenHorizontal,
                top = ReelsDimensions.fullCastScreenTopPadding,
                end = ReelsSpacing.screenHorizontal,
                bottom = ReelsSpacing.xxLarge
            ),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.detailsSectionSpacing)
    ) {
        CastDetailsStateHeader(onBackClick = onBackClick)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
        ) {
            Column(
                modifier = Modifier.padding(ReelsDimensions.stateCardPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ReelsDimensions.stateCardContentSpacing)
            ) {
                Surface(
                    modifier = Modifier.size(ReelsDimensions.stateIconContainerSize),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(ReelsDimensions.stateIconSize)
                        )
                    }
                }

                Text(
                    text = message.toErrorTitle(fallback = "Unable to load cast member"),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                FilledTonalButton(
                    modifier = Modifier.padding(top = ReelsSpacing.small),
                    onClick = onRetry
                ) {
                    Text(text = retryLabel)
                }
            }
        }
    }
}

@Composable
private fun CastDetailsStateHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ReelsSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(ReelsDimensions.categoryBackButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(ReelsDimensions.categoryBackIconSize)
                )
            }
        }

        Text(
            text = "Cast Details",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CastDetailsPlaceholderBox(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(castDetailsPlaceholderColor())
    )
}

@Composable
private fun CastDetailsPlaceholderBar(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(MaterialTheme.shapes.medium)
            .background(castDetailsPlaceholderColor())
    )
}

@Composable
private fun castDetailsPlaceholderColor(): Color {
    return MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f)
}

private const val CAST_DETAILS_KNOWN_FOR_PLACEHOLDER_COUNT = 4
