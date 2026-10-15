/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.cast.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.norbertotaveras.reels.domain.model.person.PersonMovieCreditDataModel
import com.norbertotaveras.reels.presentation.ui.theme.ReelsDimensions
import com.norbertotaveras.reels.presentation.ui.theme.ReelsSpacing
import com.norbertotaveras.reels.presentation.ui.theme.ReelsTheme

@Composable
fun CastDetailsContent(
    name: String,
    profileUrl: String?,
    biography: String,
    knownForDepartment: String?,
    knownFor: List<PersonMovieCreditDataModel>,
    birthday: String?,
    placeOfBirth: String?,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onFilmographyClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    isFavoriteLoading: Boolean = false,
    isOfflineSavedPerson: Boolean = false,
    onFavoriteClick: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = ReelsSpacing.xxLarge),
        verticalArrangement = Arrangement.spacedBy(ReelsDimensions.castDetailsSectionSpacing)
    ) {
        item {
            CastHeroHeader(
                name = name,
                profileUrl = profileUrl,
                knownForDepartment = knownForDepartment,
                isFavorite = isFavorite,
                isFavoriteLoading = isFavoriteLoading,
                isOfflineSavedPerson = isOfflineSavedPerson,
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick
            )
        }

        item {
            CastQuickInfoSection(
                birthday = birthday,
                placeOfBirth = placeOfBirth
            )
        }

        item {
            BiographySection(biography = biography)
        }

        item {
            KnownForPreviewSection(
                knownFor = knownFor,
                onMovieClick = onMovieClick
            )
        }

        item {
            FullFilmographySection(
                creditCount = knownFor.size,
                onClick = onFilmographyClick
            )
        }

        item {
            PersonalInfoSection(
                birthday = birthday,
                alsoKnownAs = name,
                knownCreditsCount = knownFor.size
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun CastDetailsContentPreview() {
    ReelsTheme {
        CastDetailsContent(
            name = "Timothée Chalamet",
            profileUrl = null,
            biography = "Timothée Chalamet is an American and French actor known for emotionally precise performances across independent dramas and large-scale films.",
            knownForDepartment = "Acting",
            knownFor = emptyList(),
            birthday = "1995-12-27",
            placeOfBirth = "New York City, New York, USA",
            onBackClick = {},
            onMovieClick = {},
            onFilmographyClick = {}
        )
    }
}
