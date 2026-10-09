/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.repository.favorites

import com.norbertotaveras.reels.data.local.dao.FavoriteMovieDao
import com.norbertotaveras.reels.data.local.mapper.toFavoriteMovieDataModel
import com.norbertotaveras.reels.data.local.mapper.toFavoriteMovieEntity
import com.norbertotaveras.reels.data.local.mapper.toMovieDetailsDataModel
import com.norbertotaveras.reels.data.repository.repositoryResult
import com.norbertotaveras.reels.di.qualifiers.IoDispatcher
import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.model.movie.CachedMovieDetailsDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.domain.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteMoviesRepositoryImpl @Inject constructor(
    private val favoriteMovieDao: FavoriteMovieDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : FavoriteMoviesRepository {
    override fun observeFavoriteMovies(): Flow<List<FavoriteMovieDataModel>> {
        return favoriteMovieDao.observeFavoriteMovies().map { favorites ->
            favorites.map { favorite -> favorite.toFavoriteMovieDataModel() }
        }.flowOn(ioDispatcher)
    }

    override fun observeIsFavorite(movieId: Int): Flow<Boolean> {
        return favoriteMovieDao.observeIsFavorite(movieId = movieId)
            .flowOn(ioDispatcher)
    }

    override suspend fun toggleFavoriteMovie(movie: MovieDetailsDataModel): Result<Unit> {
        return repositoryResult(ioDispatcher) {
            favoriteMovieDao.toggleFavoriteMovie(movie = movie.toFavoriteMovieEntity())
        }
    }

    override suspend fun removeFavoriteMovie(movieId: Int): Result<Unit> {
        return repositoryResult(ioDispatcher) {
            favoriteMovieDao.deleteFavoriteMovie(movieId = movieId)
        }
    }

    override suspend fun getFavoriteMovieDetails(movieId: Int): CachedMovieDetailsDataModel? {
        return withContext(ioDispatcher) {
            favoriteMovieDao.getFavoriteMovie(movieId = movieId)?.let { favorite ->
                CachedMovieDetailsDataModel(
                    movie = favorite.toMovieDetailsDataModel(),
                    cachedAt = favorite.favoritedAt
                )
            }
        }
    }
}
