/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.domain.repository.favorites

import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import kotlinx.coroutines.flow.Flow

interface FavoriteMoviesRepository {
    fun observeFavoriteMovies(): Flow<List<FavoriteMovieDataModel>>
    fun observeIsFavorite(movieId: Int): Flow<Boolean>
    suspend fun toggleFavoriteMovie(movie: MovieDetailsDataModel)
    suspend fun removeFavoriteMovie(movieId: Int)
    suspend fun getFavoriteMovieDetails(movieId: Int): MovieDetailsDataModel?
}
