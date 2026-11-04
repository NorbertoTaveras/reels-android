/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.library.repository

import com.norbertotaveras.reels.domain.library.model.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.movie.model.CachedMovieDetailsDataModel
import com.norbertotaveras.reels.domain.movie.model.MovieDetailsDataModel
import kotlinx.coroutines.flow.Flow

interface FavoriteMoviesRepository {
    fun observeFavoriteMovies(): Flow<List<FavoriteMovieDataModel>>
    fun observeIsFavorite(movieId: Int): Flow<Boolean>
    suspend fun toggleFavoriteMovie(movie: MovieDetailsDataModel): Result<Unit>
    suspend fun removeFavoriteMovie(movieId: Int): Result<Unit>
    suspend fun getFavoriteMovieDetails(movieId: Int): CachedMovieDetailsDataModel?
}
