package com.norbertotaveras.reels.domain.repository.favorites

import com.norbertotaveras.reels.domain.model.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.domain.model.movie.MovieDetailsDataModel
import kotlinx.coroutines.flow.Flow

interface FavoriteMoviesRepository {
    fun observeFavoriteMovies(): Flow<List<FavoriteMovieDataModel>>
    fun observeIsFavorite(movieId: Int): Flow<Boolean>
    suspend fun toggleFavoriteMovie(movie: MovieDetailsDataModel)
    suspend fun getFavoriteMovieDetails(movieId: Int): MovieDetailsDataModel?
}
