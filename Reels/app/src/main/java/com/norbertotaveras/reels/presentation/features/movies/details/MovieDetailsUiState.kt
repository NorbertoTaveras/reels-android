/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.features.movies.details

import com.norbertotaveras.reels.domain.model.cast.CastMemberDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieVideoDataModel

data class MovieDetailsUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetailsDataModel? = null,
    val cast: List<CastMemberDataModel> = emptyList(),
    val trailers: List<MovieVideoDataModel> = emptyList(),
    val similarMovies: List<MovieDataModel> = emptyList(),
    val isFavorite: Boolean = false,
    val isFavoriteLoading: Boolean = false,
    val isOfflineFavorite: Boolean = false,
    val errorMessage: String? = null
)
