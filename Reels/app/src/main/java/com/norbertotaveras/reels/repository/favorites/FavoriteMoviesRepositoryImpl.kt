package com.norbertotaveras.reels.repository.favorites

import com.norbertotaveras.reels.model.data.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.model.data.movie.MovieDetailsDataModel
import com.norbertotaveras.reels.model.local.dao.FavoriteMovieDao
import com.norbertotaveras.reels.model.local.mapper.toFavoriteMovieDataModel
import com.norbertotaveras.reels.model.local.mapper.toFavoriteMovieEntity
import com.norbertotaveras.reels.model.local.mapper.toMovieDetailsDataModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteMoviesRepositoryImpl @Inject constructor(
    private val favoriteMovieDao: FavoriteMovieDao
) : FavoriteMoviesRepository {
    override fun observeFavoriteMovies(): Flow<List<FavoriteMovieDataModel>> {
        return favoriteMovieDao.observeFavoriteMovies().map { favorites ->
            favorites.map { favorite -> favorite.toFavoriteMovieDataModel() }
        }
    }

    override fun observeIsFavorite(movieId: Int): Flow<Boolean> {
        return favoriteMovieDao.observeIsFavorite(movieId = movieId)
    }

    override suspend fun toggleFavoriteMovie(movie: MovieDetailsDataModel) {
        val existingFavorite = favoriteMovieDao.getFavoriteMovie(movieId = movie.id)

        if (existingFavorite == null) {
            favoriteMovieDao.upsertFavoriteMovie(movie.toFavoriteMovieEntity())
        } else {
            favoriteMovieDao.deleteFavoriteMovie(movieId = movie.id)
        }
    }

    override suspend fun getFavoriteMovieDetails(movieId: Int): MovieDetailsDataModel? {
        return favoriteMovieDao.getFavoriteMovie(movieId = movieId)?.toMovieDetailsDataModel()
    }
}
