package com.norbertotaveras.reels.usecases.favorites

import com.norbertotaveras.reels.model.data.favorite.FavoriteMovieDataModel
import com.norbertotaveras.reels.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteMoviesUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    operator fun invoke(): Flow<List<FavoriteMovieDataModel>> {
        return repository.observeFavoriteMovies()
    }
}
