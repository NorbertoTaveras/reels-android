package com.norbertotaveras.reels.usecases.favorites

import com.norbertotaveras.reels.repository.favorites.FavoriteMoviesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveIsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteMoviesRepository
) {
    operator fun invoke(movieId: Int): Flow<Boolean> {
        return repository.observeIsFavorite(movieId = movieId)
    }
}
