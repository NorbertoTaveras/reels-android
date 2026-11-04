/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movie.details

import com.norbertotaveras.reels.domain.movie.model.MovieDataModel
import com.norbertotaveras.reels.domain.movie.model.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.movie.model.MovieVideoDataModel
import com.norbertotaveras.reels.domain.person.model.CastMemberDataModel

data class MovieDetailsUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetailsDataModel? = null,
    val cast: List<CastMemberDataModel> = emptyList(),
    val trailers: List<MovieVideoDataModel> = emptyList(),
    val similarMovies: List<MovieDataModel> = emptyList(),
    val isFavorite: Boolean = false,
    val isFavoriteLoading: Boolean = false,
    val isOfflineContent: Boolean = false,
    val cachedAt: Long? = null,
    val savedPersonIds: Set<Int> = emptySet(),
    val savingPersonIds: Set<Int> = emptySet(),
    val isTmdbAccountConnected: Boolean = false,
    val isWatchlisted: Boolean = false,
    val tmdbRating: Double? = null,
    val selectedTmdbRating: Double? = null,
    val isRatingSheetVisible: Boolean = false,
    val isAccountActionLoading: Boolean = false,
    val errorMessage: String? = null
)
